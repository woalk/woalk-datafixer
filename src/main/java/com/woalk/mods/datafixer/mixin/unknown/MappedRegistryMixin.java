package com.woalk.mods.datafixer.mixin.unknown;

import com.woalk.mods.datafixer.DatafixerModKt;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MappedRegistry.class)
abstract class MappedRegistryMixin<T> {
    @Inject(method = "get(Lnet/minecraft/resources/Identifier;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    private void wdf$get(Identifier id, CallbackInfoReturnable<Optional<Holder.Reference<T>>> cir) {
        //noinspection unchecked
        final var self = (MappedRegistry<T>) (Object) this;
        final var result = cir.getReturnValue();

        if (result.isEmpty() && !id.getNamespace().equals(DatafixerModKt.MOD_ID)) {
            if (self.key().identifier().getPath().equals("item")) {
                DatafixerModKt.getLOGGER().warn("Found missing entry for {} in {} registry.", id, self.key().identifier().getPath());
                cir.setReturnValue(self.get(Identifier.fromNamespaceAndPath(DatafixerModKt.MOD_ID, "unknown")));
            }
        }
    }
}
