package dev.foundry.crucible.mixin.vec3;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {

    @Unique
    private static final BlockPos.MutableBlockPos crucible$ABOVE = new BlockPos.MutableBlockPos();

    @Redirect(method = "hasSameAbove", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;"))
    private static BlockPos above(BlockPos instance) {
        return crucible$ABOVE.setWithOffset(instance, 0, 1, 0);
    }
}
