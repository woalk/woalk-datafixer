package com.woalk.mods.datafixer;

import com.google.common.collect.Streams;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;
import net.minecraft.util.datafix.fixes.References;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Converts a custom sign block entity from the old {@code Text1-4} format to the new
 * {@code front_text}/{@code back_text} format, exactly like vanilla's
 * {@link net.minecraft.util.datafix.fixes.BlockEntitySignDoubleSidedEditableTextFix}.
 *
 * <p>Unlike the vanilla fix (which extends {@code NamedEntityWriteReadFix} and rewrites the
 * <em>entire</em> {@code BLOCK_ENTITY} recursive type), this fix rewrites only the targeted
 * sign's own choice type, the same way {@link net.minecraft.util.datafix.fixes.DecoratedPotFieldRenameFix}
 * does. Rewriting the whole {@code BLOCK_ENTITY} type in a custom, inserted schema subversion
 * makes DataFixerUpper's optimizer combine our rule with later whole/element block-entity
 * rewrites (e.g. the {@code minecraft:decorated_pot} rename at schema 3448) into a single
 * recursive-family pass, which then fails with {@code IllegalArgumentException: Focused type
 * doesn't match.}. Operating on a single choice type keeps the rule local and composes cleanly.
 */
public class CustomSignFix extends DataFix {
    private static final List<String> FIELDS_TO_DROP = List.of(
        "Text1", "Text2", "Text3", "Text4",
        "FilteredText1", "FilteredText2", "FilteredText3", "FilteredText4",
        "Color", "GlowingText"
    );

    private final String entityName;

    public CustomSignFix(final Schema outputSchema, final String entityName) {
        super(outputSchema, true);
        this.entityName = entityName;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        final Type<?> oldType = this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, this.entityName);
        final Type<?> newType = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, this.entityName);
        return fix(oldType, newType);
    }

    private <A, B> TypeRewriteRule fix(final Type<A> oldType, final Type<B> newType) {
        return this.fixTypeEverywhereTyped(
            "Woalk custom sign text format update for " + this.entityName,
            oldType,
            newType,
            typed -> Util.writeAndReadTypedOrThrow(typed, newType, CustomSignFix::fixData)
        );
    }

    private static <T> Dynamic<T> fixData(Dynamic<T> input) {
        input = input.set("front_text", fixFrontTextTag(input))
            .set("back_text", createDefaultText(input))
            .set("is_waxed", input.createBoolean(false))
            .set("_filtered_correct", input.createBoolean(true));

        for (String field : FIELDS_TO_DROP) {
            input = input.remove(field);
        }

        return input;
    }

    private static <T> Dynamic<T> fixFrontTextTag(final Dynamic<T> tag) {
        Dynamic<T> emptyLine = LegacyComponentDataFixUtils.createEmptyComponent(tag.getOps());
        List<Dynamic<T>> lines = getLines(tag, "Text").map(line -> line.orElse(emptyLine)).toList();
        Dynamic<T> text = tag.emptyMap()
            .set("messages", tag.createList(lines.stream()))
            .set("color", tag.get("Color").result().orElse(tag.createString("black")))
            .set("has_glowing_text", tag.get("GlowingText").result().orElse(tag.createBoolean(false)));
        List<Optional<Dynamic<T>>> filteredLines = getLines(tag, "FilteredText").toList();
        if (filteredLines.stream().anyMatch(Optional::isPresent)) {
            text = text.set("filtered_messages", tag.createList(Streams.mapWithIndex(filteredLines.stream(), (line, index) -> {
                Dynamic<T> fallbackLine = lines.get((int) index);
                return line.orElse(fallbackLine);
            })));
        }

        return text;
    }

    private static <T> Stream<Optional<Dynamic<T>>> getLines(final Dynamic<T> tag, final String linePrefix) {
        return Stream.of(
            tag.get(linePrefix + "1").result(),
            tag.get(linePrefix + "2").result(),
            tag.get(linePrefix + "3").result(),
            tag.get(linePrefix + "4").result()
        );
    }

    private static <T> Dynamic<T> createDefaultText(final Dynamic<T> tag) {
        return tag.emptyMap()
            .set("messages", createEmptyLines(tag))
            .set("color", tag.createString("black"))
            .set("has_glowing_text", tag.createBoolean(false));
    }

    private static <T> Dynamic<T> createEmptyLines(final Dynamic<T> tag) {
        Dynamic<T> emptyComponent = LegacyComponentDataFixUtils.createEmptyComponent(tag.getOps());
        return tag.createList(Stream.of(emptyComponent, emptyComponent, emptyComponent, emptyComponent));
    }
}
