package dev.foundry.crucible.mixin.client.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LeavesBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

    @Unique
    private final BlockPos.MutableBlockPos crucible$animateTickPos = new BlockPos.MutableBlockPos();

    @Redirect(method = "animateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;"))
    public BlockPos above(BlockPos instance) {
        return this.crucible$animateTickPos.setWithOffset(instance, Direction.UP);
    }

    @Redirect(method = "animateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;below()Lnet/minecraft/core/BlockPos;"))
    public BlockPos below(BlockPos instance) {
        return this.crucible$animateTickPos.setWithOffset(instance, Direction.DOWN);
    }
}
