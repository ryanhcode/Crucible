package dev.foundry.crucible.mixin.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMaps;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {

    @Unique
    private static final ThreadLocal<BlockPos.MutableBlockPos> crucible$TEMP_POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
    @Unique
    private static final Float2ObjectMap<VoxelShape> SHAPE_CACHE = Util.make(Float2ObjectMaps.synchronize(new Float2ObjectArrayMap<>()), map -> map.put(1.0F, Shapes.block()));

    @Unique
    private static boolean crucible$isFaceOccludedByState(BlockGetter level, Direction direction, VoxelShape other, BlockPos blockPos, BlockState blockState) {
        if (blockState.canOcclude()) {
            return Shapes.blockOccudes(other, blockState.getOcclusionShape(level, blockPos), direction);
        }
        return false;
    }

    @Redirect(method = "isFaceOccludedByState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/Shapes;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return SHAPE_CACHE.computeIfAbsent((float) maxY, unused -> Shapes.box(minX, minY, minZ, maxX, maxY, maxZ));
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static boolean shouldRenderFace(BlockAndTintGetter level, BlockPos blockPos, FluidState fluidState, BlockState blockState, Direction direction, FluidState fluidState2) {
        return !crucible$isFaceOccludedByState(level, direction.getOpposite(), Shapes.block(), blockPos, blockState) && !fluidState2.getType().isSame(fluidState.getType());
    }

    @Inject(method = "tesselate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockAndTintGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", shift = At.Shift.BEFORE))
    public void updateCachePos(BlockAndTintGetter level, BlockPos pos, VertexConsumer vertexConsumer, BlockState state, FluidState fluidState, CallbackInfo ci) {
        crucible$TEMP_POS.get().set(pos);
    }

    @Redirect(method = "tesselate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;relative(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos;"))
    public BlockPos relative(BlockPos instance, Direction direction) {
        return crucible$TEMP_POS.get().move(direction);
    }
}
