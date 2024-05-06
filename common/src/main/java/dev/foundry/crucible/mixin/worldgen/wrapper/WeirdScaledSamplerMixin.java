package dev.foundry.crucible.mixin.worldgen.wrapper;

import dev.foundry.crucible.worldgen.CrucibleWorldGenHook;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.world.level.levelgen.DensityFunctions.WeirdScaledSampler")
public class WeirdScaledSamplerMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), index = 1, argsOnly = true)
    private static DensityFunction init(DensityFunction value) {
        return CrucibleWorldGenHook.simplify(value);
    }
}
