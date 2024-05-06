package dev.foundry.crucible.mixin.chunk;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @Shadow
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap;

    @Unique
    private volatile Long2ObjectMap<ChunkHolder> crucible$asyncVisibleChunkMap;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor executor, BlockableEventLoop blockableEventLoop, LightChunkGetter lightChunkGetter, ChunkGenerator chunkGenerator, ChunkProgressListener chunkProgressListener, ChunkStatusUpdateListener chunkStatusUpdateListener, Supplier supplier, int i, boolean bl, CallbackInfo ci) {
        this.crucible$asyncVisibleChunkMap = this.visibleChunkMap;
    }

    @Inject(method = "promoteChunkMap", at = @At("TAIL"))
    public void promoteChunkMap(CallbackInfoReturnable<Boolean> cir) {
        this.crucible$asyncVisibleChunkMap = Long2ObjectMaps.synchronize(this.visibleChunkMap);
    }

    @Inject(method = "getVisibleChunkIfPresent", at = @At("HEAD"), cancellable = true)
    protected void getVisibleChunkIfPresent(long pos, CallbackInfoReturnable<ChunkHolder> cir) {
        cir.setReturnValue(this.crucible$asyncVisibleChunkMap.get(pos));
    }

    @Inject(method = "size", at = @At("HEAD"), cancellable = true)
    public void size(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.crucible$asyncVisibleChunkMap.size());
    }

    @Inject(method = "getChunks", at = @At("HEAD"), cancellable = true)
    public void getChunks(CallbackInfoReturnable<Iterable<ChunkHolder>> cir) {
        cir.setReturnValue(Iterables.unmodifiableIterable(this.crucible$asyncVisibleChunkMap.values()));
    }
}
