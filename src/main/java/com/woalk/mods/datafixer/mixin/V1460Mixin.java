package com.woalk.mods.datafixer.mixin;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.woalk.mods.datafixer.ConfigReader;
import net.minecraft.util.datafix.schemas.V1460;
import net.minecraft.util.datafix.schemas.V99;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(V1460.class)
abstract class V1460Mixin {

    @Inject(method = "registerBlockEntities", at = @At(value = "RETURN"))
    private void wdf$registerBlockEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        Map<String, Supplier<TypeTemplate>> map = cir.getReturnValue();
        if (map == null) {
            return;
        }

        final var signs = ConfigReader.INSTANCE.signMapping();
        final var signTemplate = map.get("minecraft:sign");

        for (var sign : signs) {
            if (signTemplate != null) {
                schema.register(map, sign, signTemplate);
            } else {
                schema.register(map, sign, () -> V99.sign(schema));
            }
        }
    }
}
