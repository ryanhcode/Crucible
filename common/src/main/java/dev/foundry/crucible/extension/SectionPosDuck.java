package dev.foundry.crucible.extension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityAccess;

public interface SectionPosDuck {

    SectionPos crucible$setX(int x);

    SectionPos crucible$setY(int y);

    SectionPos crucible$setZ(int z);

    SectionPos crucible$set(int x, int y, int z);

    default SectionPos crucible$set(BlockPos blockPos) {
        return this.crucible$set(blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4);
    }

    default SectionPos crucible$set(ChunkPos chunkPos, int i) {
        return this.crucible$set(chunkPos.x, i, chunkPos.z);
    }

    default SectionPos crucible$set(EntityAccess entityAccess) {
        return this.crucible$set(entityAccess.blockPosition());
    }

    default SectionPos crucible$set(Position position) {
        return this.crucible$set(Mth.floor(position.x()) >> 4, Mth.floor(position.y()) >> 4, Mth.floor(position.z()) >> 4);
    }

    default SectionPos crucible$set(long l) {
        return this.crucible$set((int) (l >> 42), (int) (l << 44 >> 44), (int) (l << 22 >> 42));
    }

    default SectionPos crucible$set(SectionPos pos) {
        return this.crucible$set(pos.x(), pos.y(), pos.z());
    }
}
