package dev.foundry.crucible.mixin.client.level;

import it.unimi.dsi.fastutil.ints.Int2ByteLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public class BlockMixin {

    @Unique
    private static final ThreadLocal<Int2ByteLinkedOpenHashMap> crucible$OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
        Int2ByteLinkedOpenHashMap map = new Int2ByteLinkedOpenHashMap(2048, 0.25F) {
            @Override
            protected void rehash(int i) {
            }
        };
        map.defaultReturnValue((byte) 127);
        return map;
    });

    /**
     * @author Ocelot
     * @reason Reduce memory allocations
     */
    @Overwrite
    public static boolean shouldRenderFace(BlockState state, BlockGetter level, BlockPos pos, Direction direction, BlockPos neighborPos) {
        BlockState neighborState = level.getBlockState(neighborPos);
        if (state.skipRendering(neighborState, direction)) {
            return false;
        }

        if (!neighborState.canOcclude()) {
            return true;
        }

        int hash = 961 * state.hashCode() + 31 * neighborState.hashCode() + direction.hashCode();
        byte value = crucible$OCCLUSION_CACHE.get().getAndMoveToFirst(hash);
        if (value != 127) {
            return value != 0;
        }

        VoxelShape voxelshape = state.getFaceOcclusionShape(level, pos, direction);
        if (voxelshape.isEmpty()) {
            return true;
        }

        Int2ByteLinkedOpenHashMap cache = crucible$OCCLUSION_CACHE.get();
        boolean shouldRender = Shapes.joinIsNotEmpty(voxelshape, neighborState.getFaceOcclusionShape(level, neighborPos, direction.getOpposite()), BooleanOp.ONLY_FIRST);
        while (cache.size() >= 2048) {
            cache.removeLastByte();
        }

        cache.putAndMoveToFirst(hash, (byte) (shouldRender ? 1 : 0));
        return shouldRender;
    }
}
