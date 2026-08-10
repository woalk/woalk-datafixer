package com.woalk.mods.datafixer.mixin;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.woalk.mods.datafixer.Config;
import com.woalk.mods.datafixer.CustomSignFix;
import com.woalk.mods.datafixer.MySchema2;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.BlockEntitySignDoubleSidedEditableTextFix;
import net.minecraft.util.filefix.FileFixerUpper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixers.class)
abstract class DataFixersMixin {

    /**
     * Add datafixing support for modded signs.
     *
     * <p>Vanilla converts each sign type (the old {@code Text1-4} format to the new
     * {@code front_text}/{@code back_text} format) in its own isolated schema version:
     * {@code minecraft:sign} at schema {@code 3439} and {@code minecraft:hanging_sign} at
     * schema {@code 3439} subversion {@code 1}. This isolation is required because
     * {@link BlockEntitySignDoubleSidedEditableTextFix} (a {@code NamedEntityWriteReadFix})
     * rewrites the <em>entire</em> {@code BLOCK_ENTITY} type from the input schema to the
     * output schema and only transforms the targeted id, blindly casting every other block
     * entity type. If a second sign id changes its type in the same schema version as
     * {@code minecraft:sign}, that cast becomes structurally invalid and DFU throws
     * {@code Either$Left cannot be cast to Pair} while loading any sign.
     *
     * <p>Therefore the modded signs are kept in the old format through {@code V1460Mixin}
     * (so they stay consistent while vanilla converts {@code minecraft:sign} and
     * {@code minecraft:hanging_sign}) and are converted here in their own dedicated schema
     * version, injected right after vanilla's {@code minecraft:hanging_sign} fixer.
     *
     * <p>The conversion itself is performed by {@link CustomSignFix}, which rewrites only the
     * targeted sign's own choice type (like vanilla's {@code DecoratedPotFieldRenameFix})
     * instead of the whole {@code BLOCK_ENTITY} type. Using the whole-type
     * {@link BlockEntitySignDoubleSidedEditableTextFix} here makes DataFixerUpper's optimizer
     * merge our rule with later block-entity type changes (e.g. the {@code decorated_pot}
     * rename at schema 3448) into a single recursive-family pass that fails with
     * {@code IllegalArgumentException: Focused type doesn't match.}.
     */
    @Inject(method = "addFixers", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/datafix/fixes/BlockEntitySignDoubleSidedEditableTextFix;<init>(Lcom/mojang/datafixers/schemas/Schema;Ljava/lang/String;Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.AFTER))
    private static void wdf$addFixers(DataFixerBuilder fixerUpper, FileFixerUpper.Builder fileFixerUpper, CallbackInfo ci) {
        final var signs = Config.Companion.getInstance().signMapping();
        if (signs.isEmpty()) {
            return;
        }

        int subVersion = 2;
        for (var sign : signs) {
            final Schema schema = fixerUpper.addSchema(3439, subVersion++, MySchema2::new);
            fixerUpper.addFixer(new CustomSignFix(schema, sign));
        }
    }
}
