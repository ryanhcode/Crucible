package dev.foundry.crucible.mixin.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.foundry.crucible.extension.PoseStackDuck;
import dev.foundry.crucible.fog.GlobalPoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Unique
    private final GuiGraphics crucible$guiGraphics = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());

    @Unique
    private final Matrix4f crucible$projection = new Matrix4f();

    @Redirect(method = "renderZoomed", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    public PoseStack renderZoomedStack() {
        return GlobalPoseStack.get();
    }

    @Redirect(method = "getProjectionMatrix", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    public PoseStack getProjectionMatrixStack() {
        return GlobalPoseStack.get();
    }

    @Inject(method = "getProjectionMatrix", at = @At("RETURN"), cancellable = true)
    public void getProjectionMatrix(double d, CallbackInfoReturnable<Matrix4f> cir) {
        cir.setReturnValue(this.crucible$projection.set(cir.getReturnValue()));
    }

    @Redirect(method = "render", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    public PoseStack renderStack() {
        return GlobalPoseStack.get();
    }

    @Redirect(method = "renderLevel", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    public PoseStack renderLevelStack() {
        return GlobalPoseStack.get();
    }

    @Redirect(method = "renderItemActivationAnimation", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    public PoseStack renderItemActivationAnimationStack() {
        return GlobalPoseStack.get();
    }

    @Redirect(method = "render", at = @At(value = "NEW", target = "(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)Lnet/minecraft/client/gui/GuiGraphics;"))
    public GuiGraphics create(Minecraft arg, MultiBufferSource.BufferSource arg2) {
        ((PoseStackDuck) this.crucible$guiGraphics.pose()).reset();
        return this.crucible$guiGraphics;
    }
}
