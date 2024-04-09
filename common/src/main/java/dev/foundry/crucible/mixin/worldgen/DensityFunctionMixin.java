package dev.foundry.crucible.mixin.worldgen;

import dev.foundry.crucible.extension.DensityFunctionDuck;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DensityFunction.class)
public interface DensityFunctionMixin extends DensityFunctionDuck {

    @Shadow
    double compute(DensityFunction.FunctionContext arg);

    @Shadow
    void fillArray(double[] ds, DensityFunction.ContextProvider arg);

    @Override
    default double crucible$computeDensity(DensityFunction.FunctionContext context) {
        return this.compute(context);
    }

    @Override
    default void crucible$computeDensity(double[] fill, DensityFunction.ContextProvider context) {
        this.fillArray(fill, context);
    }
}
