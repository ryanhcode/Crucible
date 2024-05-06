package dev.foundry.crucible.mixin.chunk;

import com.mojang.datafixers.util.Either;
import dev.foundry.crucible.world.CrucibleChunkCache;
import net.minecraft.Util;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {

    @Shadow
    @Final
    public ServerLevel level;

    @Shadow
    protected abstract CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFutureMainThread(int k, int l, ChunkStatus arg, boolean bl);

    @Shadow
    @Final
    private ServerChunkCache.MainThreadExecutor mainThreadProcessor;

    @Shadow
    @Final
    Thread mainThread;

    @Shadow
    @Nullable
    protected abstract ChunkHolder getVisibleChunkIfPresent(long l);

    @Shadow
    protected abstract void storeInCache(long l, ChunkAccess arg, ChunkStatus arg2);

    @Shadow
    protected abstract boolean chunkAbsent(ChunkHolder arg, int i);

    @Shadow
    @Final
    private static List<ChunkStatus> CHUNK_STATUSES;
    @Unique
    private final CrucibleChunkCache crucible$cache = new CrucibleChunkCache(4);

    /**
     * @reason Allow cache use from other threads
     * @author Ocelot
     */
    @Overwrite
    public ChunkAccess getChunk(int x, int z, ChunkStatus chunkStatus, boolean now) {
        long pos = ChunkPos.asLong(x, z);
        ChunkAccess access = this.crucible$cache.get(pos, chunkStatus);
        if (access != null) {
            return access;
        }

        CompletableFuture<ChunkAccess> future = this.getChunkFutureMainThread(x, z, chunkStatus, now).thenApply(either -> either.map(value -> {
            this.crucible$cache.storeInCache(pos, value, chunkStatus);
            return value;
        }, failure -> {
            if (now) {
                throw Util.pauseInIde(new IllegalStateException("Chunk not there when requested: " + failure));
            } else {
                return null;
            }
        }));

        // On the main thread make sure the chunk is loaded
        if (Thread.currentThread() == this.mainThread) {
            this.mainThreadProcessor.managedBlock(future::isDone);
        }

        // On other threads this will block until the chunk is generated
        return future.join();
    }

    /**
     * @reason Allow cache use from other threads
     * @author Ocelot
     */
    @Overwrite
    public LevelChunk getChunkNow(int x, int z) {
        long pos = ChunkPos.asLong(x, z);
        ChunkAccess access = this.crucible$cache.get(pos, ChunkStatus.FULL);
        if (access != null) {
            return access instanceof LevelChunk chunk ? chunk : null;
        }

        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(pos);
        if (chunkHolder == null) {
            return null;
        }

        Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> either = chunkHolder.getFutureIfPresent(ChunkStatus.FULL).getNow(null);
        if (either == null) {
            return null;
        }

        ChunkAccess left = either.left().orElse(null);
        if (left != null) {
            this.storeInCache(pos, left, ChunkStatus.FULL);
            if (left instanceof LevelChunk chunk) {
                return chunk;
            }
        }

        return null;
    }

    /**
     * @reason Thread safety
     * @author Ocelot
     */
    @Overwrite
    private void clearCache() {
        this.crucible$cache.clear();
    }

    /**
     * @reason Don't block thread when scheduling. DistanceManager is thread-safe now, so this works fine
     * @author Ocelot
     */
    @Overwrite
    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFuture(int x, int z, ChunkStatus chunkStatus, boolean now) {
        return this.getChunkFutureMainThread(x, z, chunkStatus, now);
    }

    /**
     * @reason Don't block thread when scheduling. DistanceManager is thread-safe now, so this works fine
     * @author Ocelot
     */
    @Overwrite
    public boolean hasChunk(int x, int z) {
        return !this.chunkAbsent(this.getVisibleChunkIfPresent(ChunkPos.asLong(x, z)), ChunkLevel.byStatus(ChunkStatus.FULL));
    }

    /**
     * @reason Don't block thread when scheduling. DistanceManager is thread-safe now, so this works fine
     * @author Ocelot
     */
    @Overwrite
    public @Nullable LightChunk getChunkForLighting(int x, int z) {
        long pos = ChunkPos.asLong(x, z);
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(pos);
        if (chunkHolder == null) {
            return null;
        }

        int k = CHUNK_STATUSES.size() - 1;

        while (true) {
            ChunkStatus chunkStatus = CHUNK_STATUSES.get(k);
            Optional<ChunkAccess> optional = chunkHolder.getFutureIfPresentUnchecked(chunkStatus).getNow(ChunkHolder.UNLOADED_CHUNK).left();
            if (optional.isPresent()) {
                return optional.get();
            }

            if (chunkStatus == ChunkStatus.INITIALIZE_LIGHT.getParent()) {
                return null;
            }

            --k;
        }
    }
}
