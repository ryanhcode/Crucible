package dev.foundry.crucible.worldgen;

import dev.foundry.crucible.Crucible;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;

public class CrucibleWorldGenHook {

    private static CrucibleDensityFunctionCompiler compiler;

    /**
     * Dispose of the compiler to delete all density functions when leaving the level.
     */
    public static void clear() {
        compiler = null;
    }

    public static DensityFunction simplify(DensityFunction function) {
        if (compiler == null) {
            compiler = new CrucibleDensityFunctionCompiler(Crucible.class.getClassLoader(), true);
        }
        return compiler.compile(function);
    }

    public static NoiseRouter simplify(NoiseRouter router) {
        return new NoiseRouter(
                simplify(router.barrierNoise()),
                simplify(router.fluidLevelFloodednessNoise()),
                simplify(router.fluidLevelSpreadNoise()),
                simplify(router.lavaNoise()),
                simplify(router.temperature()),
                simplify(router.vegetation()),
                simplify(router.continents()),
                simplify(router.erosion()),
                simplify(router.depth()),
                simplify(router.ridges()),
                simplify(router.initialDensityWithoutJaggedness()),
                simplify(router.finalDensity()),
                simplify(router.veinToggle()),
                simplify(router.veinRidged()),
                simplify(router.veinGap()));
    }
}
