package dev.foundry.crucible.world;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CrucibleChunkCache {

    private final Element[] cache;
    private final int maxSize;

    public CrucibleChunkCache(int size) {
        this.cache = new Element[size];
        this.maxSize = size;
    }

    public synchronized void storeInCache(int x, int z, ChunkAccess access, ChunkStatus status) {
        System.arraycopy(this.cache, 0, this.cache, 1, this.maxSize - 1);
        this.cache[0] = new Element(x, z, access, status);
    }

    public synchronized @Nullable Element get(int x, int z, ChunkStatus status) {
        for (Element element : this.cache) {
            if (element == null || (element.x == x && element.z == z && element.status == status)) {
                return element;
            }
        }
        return null;
    }

    public synchronized void clear() {
        Arrays.fill(this.cache, null);
    }

    public record Element(int x, int z, ChunkAccess access, ChunkStatus status) {
    }
}
