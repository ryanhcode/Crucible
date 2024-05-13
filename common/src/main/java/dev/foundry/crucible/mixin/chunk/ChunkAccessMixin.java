package dev.foundry.crucible.mixin.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements BlockGetter {

    @Shadow
    @Final
    protected LevelHeightAccessor levelHeightAccessor;

    @Override
    public int getMaxBuildHeight() {
        return this.levelHeightAccessor.getMaxBuildHeight();
    }

    @Override
    public int getSectionsCount() {
        return this.levelHeightAccessor.getSectionsCount();
    }

    @Override
    public int getMinSection() {
        return this.levelHeightAccessor.getMinSection();
    }

    @Override
    public int getMaxSection() {
        return this.levelHeightAccessor.getMaxSection();
    }

    @Override
    public boolean isOutsideBuildHeight(BlockPos pos) {
        return this.levelHeightAccessor.isOutsideBuildHeight(pos);
    }

    @Override
    public boolean isOutsideBuildHeight(int blockY) {
        return this.levelHeightAccessor.isOutsideBuildHeight(blockY);
    }

    @Override
    public int getSectionIndex(int blockY) {
        return this.levelHeightAccessor.getSectionIndex(blockY);
    }

    @Override
    public int getSectionIndexFromSectionY(int sectionY) {
        return this.levelHeightAccessor.getSectionIndexFromSectionY(sectionY);
    }

    @Override
    public int getSectionYFromSectionIndex(int index) {
        return this.levelHeightAccessor.getSectionYFromSectionIndex(index);
    }
}
