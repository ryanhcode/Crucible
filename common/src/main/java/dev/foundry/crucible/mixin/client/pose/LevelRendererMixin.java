package dev.foundry.crucible.mixin.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.foundry.crucible.extension.FrustumDuck;
import dev.foundry.crucible.fog.GlobalPoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Unique
    private Frustum crucible$frustum;

    @Redirect(method = "renderLineBox(Lcom/mojang/blaze3d/vertex/VertexConsumer;DDDDDDFFFF)V", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    private static PoseStack poseStack() {
        return GlobalPoseStack.get();
    }

    @Redirect(method = "prepareCullFrustum", at = @At(value = "NEW", target = "(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)Lnet/minecraft/client/renderer/culling/Frustum;"))
    public Frustum frustum(Matrix4f modelView, Matrix4f projection) {
        if (this.crucible$frustum != null) {
            ((FrustumDuck) this.crucible$frustum).crucible$reset(modelView, projection);
        } else {
            this.crucible$frustum = new Frustum(modelView, projection);
        }
        return this.crucible$frustum;
    }
}
