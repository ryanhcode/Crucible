package dev.foundry.crucible.mixin.chunk;

import net.minecraft.server.level.ChunkTracker;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkTracker.class)
public abstract class ChunkTrackerMixin extends DynamicGraphMinFixedPoint {

    @Unique
    private final Object crucible$sync = new Object();

    protected ChunkTrackerMixin(int i, int j, int k) {
        super(i, j, k);
    }

    /**
     * @author Ocelot
     * @reason Make this thread-safe
     */
    @Overwrite
    public void update(long l, int i, boolean bl) {
        synchronized (this.crucible$sync) {
            this.checkEdge(ChunkPos.INVALID_CHUNK_POS, l, i, bl);
        }
    }
}
