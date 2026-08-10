package com.woalk.mods.datafixer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.woalk.mods.datafixer.ConfigReader;
import com.woalk.mods.datafixer.MySchema;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.BlockEntitySignDoubleSidedEditableTextFix;
import net.minecraft.util.filefix.FileFixerUpper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixers.class)
abstract class DataFixersMixin {
    @Inject(method = "addFixers", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/datafix/fixes/BlockEntitySignDoubleSidedEditableTextFix;<init>(Lcom/mojang/datafixers/schemas/Schema;Ljava/lang/String;Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER))
    private static void wdf$addFixers(DataFixerBuilder fixerUpper, FileFixerUpper.Builder fileFixerUpper, CallbackInfo ci, @Local(name = "v3439") Schema v3439) {
        final var signs = ConfigReader.INSTANCE.signMapping();
        for (var sign : signs) {
            fixerUpper.addFixer(new BlockEntitySignDoubleSidedEditableTextFix(v3439, "Woalk Sign Updater " + sign, sign));
        }
    }

    @Inject(method = "addFixers", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/datafix/fixes/EntityPaintingMotiveFix;<init>(Lcom/mojang/datafixers/schemas/Schema;Z)V", ordinal = 0, shift = At.Shift.AFTER))
    private static void wdf$addSchema(DataFixerBuilder fixerUpper, FileFixerUpper.Builder fileFixerUpper, CallbackInfo ci) {
        fixerUpper.addSchema(1460, 1, MySchema::new);
    }
}
