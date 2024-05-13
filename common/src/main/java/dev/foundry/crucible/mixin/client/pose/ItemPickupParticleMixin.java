package dev.foundry.crucible.mixin.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.foundry.crucible.fog.GlobalPoseStack;
import net.minecraft.client.particle.ItemPickupParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemPickupParticle.class)
public class ItemPickupParticleMixin {

    @Redirect(method = "render", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    public PoseStack poseStack() {
        return GlobalPoseStack.get();
    }
}
