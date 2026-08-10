package com.woalk.mods.datafixer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.woalk.mods.datafixer.ConfigReader;
import net.minecraft.util.datafix.schemas.V3439;
import net.minecraft.util.datafix.schemas.V99;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(V3439.class)
abstract class V3439Mixin {
    @Inject(method = "registerBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/datafix/schemas/V3439;register(Ljava/util/Map;Ljava/lang/String;Ljava/util/function/Supplier;)V"))
    private void wdf$registerBlockEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir, @Local(name = "map") Map<String, Supplier<TypeTemplate>> map) {
        final var signs = ConfigReader.INSTANCE.signMapping();
        for (var sign : signs) {
            schema.register(map, sign, () -> V3439.sign(schema));
        }
    }
}
