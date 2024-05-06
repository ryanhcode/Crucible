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
    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Overwrite
    public void updateForZ(int z, double noise) {
        this.inCellZ = z - this.cellStartBlockZ;
        this.interpolationCounter++;
        for (int i = 0; i < this.interpolators.size(); i++) {
            this.interpolators.get(i).updateForZ(noise);
        }
    }

    /**
     * @author Ocelot
     * @reason Remove ArrayList#forEach
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Overwrite
    public void updateForY(int y, double noise) {
        this.inCellY = y - this.cellStartBlockY;
        for (int i = 0; i < this.interpolators.size(); i++) {
            this.interpolators.get(i).updateForY(noise);
        }
    }

    /**
     * @author Ocelot
     * @reason Remove ArrayList#forEach
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Overwrite
    public void updateForX(int x, double noise) {
        this.inCellX = x - this.cellStartBlockX;
        for (int i = 0; i < this.interpolators.size(); i++) {
            this.interpolators.get(i).updateForX(noise);
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public void crucible$updateNoise(int x, int y, int z, double noise) {
        this.inCellY = y - this.cellStartBlockY;
        this.inCellX = x - this.cellStartBlockX;
        this.inCellZ = z - this.cellStartBlockZ;
        this.interpolationCounter++;
        for (int i = 0; i < this.interpolators.size(); i++) {
            NoiseChunk.NoiseInterpolator interpolator = this.interpolators.get(i);
            interpolator.updateForY(noise);
            interpolator.updateForX(noise);
            interpolator.updateForZ(noise);
        }
    }
}
