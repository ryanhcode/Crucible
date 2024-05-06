package dev.foundry.crucible.mixin.worldgen;

import dev.foundry.crucible.extension.DensityFunctionDuck;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;

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
    default double[] crucible$computeDensity(int length, DensityFunction.ContextProvider context) {
        double[] fill = new double[length];
        this.fillArray(fill, context);
        return fill;
    }
}
