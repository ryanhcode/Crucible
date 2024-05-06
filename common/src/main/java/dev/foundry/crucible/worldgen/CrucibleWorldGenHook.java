package dev.foundry.crucible.worldgen;

import dev.foundry.crucible.Crucible;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CrucibleWorldGenHook {

    private static CrucibleDensityFunctionCompiler compiler;

    /**
     * Dispose of the compiler to delete all density functions when leaving the level.
     */
    public static void clear() {
        compiler = null;
    }

    public static void init(RegistryAccess registryAccess) {
        if (compiler == null) {
            compiler = new CrucibleDensityFunctionCompiler(Crucible.class.getClassLoader(), registryAccess, true);
            Crucible.LOGGER.info("Initialized density function compiler");
        } else {
            Crucible.LOGGER.warn("Attempted to re-initialize density function compiler");
        }
    }

    public static DensityFunction simplify(DensityFunction function) {
//        return compiler != null ? compiler.compile(function) : function;
        return function;
    }
}
