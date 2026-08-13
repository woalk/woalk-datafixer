package com.woalk.mods.datafixer;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignDataFixTest {
    @BeforeAll
    static void boot() {
        Config.Companion.setReplacementConfig(TestConfigObject.INSTANCE);
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static Dynamic<?> signId(String id) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putString("Text1", "{\"text\":\"hi\"}");
        tag.putString("Text2", "{\"text\":\"\"}");
        tag.putString("Text3", "{\"text\":\"\"}");
        tag.putString("Text4", "{\"text\":\"\"}");
        return new Dynamic<>(NbtOps.INSTANCE, tag);
    }

    private static Dynamic<?> sign() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", "ecologics:sign");
        tag.putString("Text1", "{\"text\":\"hi\"}");
        tag.putString("Text2", "{\"text\":\"\"}");
        tag.putString("Text3", "{\"text\":\"\"}");
        tag.putString("Text4", "{\"text\":\"\"}");
        return new Dynamic<>(NbtOps.INSTANCE, tag);
    }

    private static void verifySignUpdate(DataFixer fixer, String id, int from, int to) {
        Dynamic<?> input = signId(id);
        Dynamic<?> out = fixer.update(References.BLOCK_ENTITY, input, from, to);
        System.out.println("DEBUG " + id + " " + from + "->" + to + ": " + out.getValue());

        assertTrue(out.get("front_text").result().isPresent(), "front_text missing for " + id + " (" + from + "->" + to + ")");
        assertTrue(out.get("back_text").result().isPresent(), "back_text missing for " + id + " (" + from + "->" + to + ")");
        assertTrue(out.get("is_waxed").result().isPresent(), "is_waxed missing for " + id + " (" + from + "->" + to + ")");
        assertTrue(out.get("_filtered_correct").result().isPresent(), "_filtered_correct missing for " + id + " (" + from + "->" + to + ")");

        Dynamic<?> frontText = out.get("front_text").orElseEmptyMap();
        assertEquals("{\"text\":\"hi\"}", frontText.get("messages").asList(d -> d.asString("")).get(0),
                "Text1 not preserved correctly in front_text for " + id);

        if (out.get("Text1").result().isPresent()) {
            System.out.println("WARN " + id + " still has Text1!");
        }
    }

    @Test
    void updatesCustomSignAcrossSchema() {
        DataFixer fixer = DataFixers.getDataFixer();

        // 3438 is before the sign format change (3439)
        // 3450 is after the sign format change
        verifySignUpdate(fixer, "ecologics:sign", 3438, 3450);
        verifySignUpdate(fixer, "minecraft:sign", 3438, 3450);
    }
}
