package dev.foundry.crucible.extension;

import net.minecraft.world.level.levelgen.DensityFunction;

@SuppressWarnings("unused") // Referenced in ASM
public interface DensityFunctionDuck {

    double crucible$computeDensity(DensityFunction.FunctionContext context);

    double[] crucible$computeDensity(int length, DensityFunction.ContextProvider context);
}
