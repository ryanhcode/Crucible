package dev.foundry.crucible.world;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CrucibleChunkCache {

    private final ArrayList<Element> cache;
    private final int maxSize;

    public CrucibleChunkCache(int size) {
        this.cache = new ArrayList<>(size + 1);
        this.maxSize = size;
    }

    public synchronized void storeInCache(long pos, ChunkAccess access, ChunkStatus status) {
        this.cache.add(new Element(pos, access, status));
        while (this.cache.size() > this.maxSize) {
            this.cache.remove(0);
        }
        this.cache.trimToSize();
    }

    public synchronized @Nullable ChunkAccess get(long pos, ChunkStatus status) {
        for (Element element : this.cache) {
            if (element.pos == pos && element.status == status) {
                return element.access;
            }
        }
        return null;
    }

    public synchronized void clear() {
        this.cache.clear();
    }

    public record Element(long pos, ChunkAccess access, ChunkStatus status) {
    }
}
