package dev.foundry.crucible.mixin.client.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpreadingSnowyDirtBlock.class)
public class SpreadingSnowyDirtBlockMixin {

    @Unique
    private static final BlockPos.MutableBlockPos crucible$TEMP = new BlockPos.MutableBlockPos();

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;"))
    private BlockPos above(BlockPos instance) {
        return crucible$TEMP.setWithOffset(instance, Direction.UP);
    }

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"))
    private BlockPos offset(BlockPos instance, int x, int y, int z) {
        return crucible$TEMP.setWithOffset(instance, x, y, z);
    }
}
