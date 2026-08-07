package com.woalk.mods.datafixer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.woalk.mods.datafixer.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.Strategy;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PalettedContainerFactory.class)
abstract class PalettedContainerFactoryMixin {    @Final
    @Shadow
    private Strategy<BlockState> blockStatesStrategy;

    @Inject(method = "createForBlockStates", at = @At("HEAD"), cancellable = true)
    private void wdf$createForBlockState(CallbackInfoReturnable<PalettedContainer<BlockState>> cir) {
        cir.setReturnValue(
                new PalettedContainer<>(
                        ModBlocks.INSTANCE.getUnknownBlock().defaultBlockState(),
                        this.blockStatesStrategy
                )
        );
    }

    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"))
    private static BlockState wdf$create(Block instance) {
        return ModBlocks.INSTANCE.getUnknownBlock().defaultBlockState();
    }
}
