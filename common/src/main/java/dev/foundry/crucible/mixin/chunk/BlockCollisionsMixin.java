package dev.foundry.crucible.mixin.chunk;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(BlockCollisions.class)
public class BlockCollisionsMixin {

    @Shadow
    @Nullable
    private BlockGetter cachedBlockGetter;

    @Shadow
    @Final
    private CollisionGetter collisionGetter;

    @Unique
    private int crucible$cachedBlockGetterX;
    @Unique
    private int crucible$cachedBlockGetterZ;

    /**
     * @author Ocelot
     * @reason Inline section pos and don't compress cache position
     */
    @Overwrite
    private @Nullable BlockGetter getChunk(int x, int z) {
        int sectionX = x >> SectionPos.SECTION_BITS;
        int sectionZ = z >> SectionPos.SECTION_BITS;
        if (this.cachedBlockGetter != null && this.crucible$cachedBlockGetterX == sectionX && this.crucible$cachedBlockGetterZ == sectionZ) {
            return this.cachedBlockGetter;
        }

        this.crucible$cachedBlockGetterX = sectionX;
        this.crucible$cachedBlockGetterZ = sectionZ;
        return this.cachedBlockGetter = this.collisionGetter.getChunkForCollisions(sectionX, sectionZ);
    }
}
