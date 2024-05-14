package dev.foundry.crucible.mixin;

import dev.foundry.crucible.extension.LevelDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor, LevelDuck {

    @Shadow
    public abstract DimensionType dimensionType();

    @Shadow
    protected int randValue;
    @Unique
    private int crucible$cachedMinY;

    @Unique
    private int crucible$cachedHeight;

    @Unique
    private float crucible$cachedAmbientLight;

    @Unique
    private boolean crucible$cachedHasFixedTime;

    @Unique
    private boolean crucible$cachedHasSkylight;

    @Unique
    private boolean crucible$cachedHasCeiling;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(WritableLevelData writableLevelData, ResourceKey resourceKey, RegistryAccess registryAccess, Holder holder, Supplier supplier, boolean bl, boolean bl2, long l, int i, CallbackInfo ci) {
        this.crucible$updateCache();
    }

    @Unique
    private void crucible$updateCache() {
        DimensionType dimensionType = this.dimensionType();
        this.crucible$cachedAmbientLight = dimensionType.ambientLight();
        this.crucible$cachedMinY = dimensionType.minY();
        this.crucible$cachedHeight = dimensionType.height();
        this.crucible$cachedHasFixedTime = dimensionType.hasFixedTime();
        this.crucible$cachedHasSkylight = dimensionType.hasSkyLight();
        this.crucible$cachedHasCeiling = dimensionType.hasCeiling();
    }

    @Override
    public ChunkAccess getChunk(BlockPos blockPos) {
        return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, true);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public LevelChunk getChunkAt(BlockPos blockPos) {
        return (LevelChunk) this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, true);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public LevelChunk getChunk(int x, int z) {
        return (LevelChunk) this.getChunk(x, z, ChunkStatus.FULL, true);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public @Nullable BlockGetter getChunkForCollisions(int x, int z) {
        return this.getChunkSource().getChunk(x, z, ChunkStatus.FULL, false);
    }

    @Override
    public int getMinBuildHeight() {
        return this.crucible$cachedMinY;
    }

    @Override
    public int getHeight() {
        return this.crucible$cachedHeight;
    }

    @Override
    public int getMaxBuildHeight() {
        return this.crucible$cachedMinY + this.crucible$cachedHeight;
    }

    @Override
    public int getMinSection() {
        return this.crucible$cachedMinY >> SectionPos.SECTION_BITS;
    }

    @Override
    public int getMaxSection() {
        return ((this.crucible$cachedMinY + this.crucible$cachedHeight - 1) >> SectionPos.SECTION_BITS) + 1;
    }

    // This seems like a copy, but this is called millions of times and the small overhead of copying parameters adds up
    @Override
    public boolean isOutsideBuildHeight(BlockPos blockPos) {
        int y = blockPos.getY() - this.crucible$cachedMinY;
        return y < 0 || y >= this.crucible$cachedHeight;
    }

    @Override
    public boolean isOutsideBuildHeight(int y) {
        y -= this.crucible$cachedMinY;
        return y < 0 || y >= this.crucible$cachedHeight;
    }

    @Override
    public int getSectionsCount() {
        return ((this.crucible$cachedHeight - 1) >> SectionPos.SECTION_BITS) + 1;
    }

    @Override
    public int getSectionIndex(int y) {
        return (y - this.crucible$cachedMinY) >> SectionPos.SECTION_BITS;
    }

    @Override
    public int getSectionIndexFromSectionY(int i) {
        return i - this.getMinSection();
    }

    @Override
    public int getSectionYFromSectionIndex(int i) {
        return i + this.getMinSection();
    }

    @Override
    public float getLightLevelDependentMagicValue(BlockPos blockPos) {
        float brightness = (float) this.getMaxLocalRawBrightness(blockPos) / 15.0F;
        return Mth.lerp(this.crucible$cachedAmbientLight, brightness / (4.0F - 3.0F * brightness), 1.0F);
    }

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasFixedTime()Z"))
    public boolean hasFixedTime(DimensionType instance) {
        return this.crucible$cachedHasFixedTime;
    }

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasSkyLight()Z"))
    public boolean hasSkyLight(DimensionType instance) {
        return this.crucible$cachedHasSkylight;
    }

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasCeiling()Z"))
    public boolean hasCeiling(DimensionType instance) {
        return this.crucible$cachedHasCeiling;
    }

    @Override
    public BlockPos.MutableBlockPos crucible$getBlockRandomPos(int x, int y, int z, int l, BlockPos.MutableBlockPos set) {
        this.randValue = this.randValue * 3 + 1013904223;
        int m = this.randValue >> 2;
        return set.set(x + (m & 15), y + (m >> 16 & l), z + (m >> 8 & 15));
    }
}
