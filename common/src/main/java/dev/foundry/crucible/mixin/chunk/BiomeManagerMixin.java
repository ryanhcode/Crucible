package dev.foundry.crucible.mixin.chunk;

import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BiomeManager.class)
public class BiomeManagerMixin {

    /**
     * @author Ocelot
     * @reason Create less variables to improve performance
     */
    @Overwrite
    private static double getFiddledDistance(long seed, int i, int j, int k, double d, double e, double f) {
        long m = LinearCongruentialGenerator.next(seed, i);
        m = LinearCongruentialGenerator.next(m, j);
        m = LinearCongruentialGenerator.next(m, k);
        m = LinearCongruentialGenerator.next(m, i);
        m = LinearCongruentialGenerator.next(m, j);
        m = LinearCongruentialGenerator.next(m, k);
        double g = (Math.floorMod(m >> 24, 1024L) / 1024.0 - 0.5) * 0.9;
        m = LinearCongruentialGenerator.next(m, seed);
        double h = (Math.floorMod(m >> 24, 1024L) / 1024.0 - 0.5) * 0.9;
        m = LinearCongruentialGenerator.next(m, seed);
        double n = (Math.floorMod(m >> 24, 1024L) / 1024.0 - 0.5) * 0.9;
        return (f + n) * (f + n) + (e + h) * (e + h) + (d + g) * (d + g);
    }
}
