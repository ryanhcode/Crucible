package dev.foundry.crucible.extension;

import net.minecraft.world.level.levelgen.DensityFunction;

public interface DensityFunctionDuck {

    double crucible$computeDensity(DensityFunction.FunctionContext context);

    void crucible$computeDensity(double[] fill, DensityFunction.ContextProvider context);
}
