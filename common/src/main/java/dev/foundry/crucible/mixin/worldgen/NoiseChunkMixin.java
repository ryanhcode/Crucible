package dev.foundry.crucible.mixin.worldgen;

import dev.foundry.crucible.extension.NoiseChunkDuck;
import net.minecraft.world.level.levelgen.NoiseChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(NoiseChunk.class)
public class NoiseChunkMixin implements NoiseChunkDuck {

    @Shadow
    @Final
    List<NoiseChunk.NoiseInterpolator> interpolators;

    @Shadow
    int inCellZ;

    @Shadow
    private int cellStartBlockZ;

    @Shadow
    long interpolationCounter;

    @Shadow
    int inCellX;

    @Shadow
    private int cellStartBlockX;

    @Shadow
    int cellStartBlockY;

    @Shadow
    int inCellY;

    /**
     * @author Ocelot
     * @reason Remove ArrayList#forEach
     */
    @Overwrite
    public void updateForZ(int z, double noise) {
        this.inCellZ = z - this.cellStartBlockZ;
        this.interpolationCounter++;
        for (NoiseChunk.NoiseInterpolator interpolator : this.interpolators) {
            interpolator.updateForZ(noise);
        }
    }

    /**
     * @author Ocelot
     * @reason Remove ArrayList#forEach
     */
    @Overwrite
    public void updateForY(int y, double noise) {
        this.inCellY = y - this.cellStartBlockY;
        for (NoiseChunk.NoiseInterpolator interpolator : this.interpolators) {
            interpolator.updateForY(noise);
        }
    }

    /**
     * @author Ocelot
     * @reason Remove ArrayList#forEach
     */
    @Overwrite
    public void updateForX(int x, double noise) {
        this.inCellX = x - this.cellStartBlockX;
        for (NoiseChunk.NoiseInterpolator interpolator : this.interpolators) {
            interpolator.updateForX(noise);
        }
    }

    @Override
    public void crucible$updateNoise(int x, int y, int z, double noise) {
        this.inCellY = y - this.cellStartBlockY;
        this.inCellX = x - this.cellStartBlockX;
        this.inCellZ = z - this.cellStartBlockZ;
        this.interpolationCounter++;
        for (NoiseChunk.NoiseInterpolator interpolator : this.interpolators) {
            interpolator.updateForY(noise);
            interpolator.updateForX(noise);
            interpolator.updateForZ(noise);
        }
    }
}
