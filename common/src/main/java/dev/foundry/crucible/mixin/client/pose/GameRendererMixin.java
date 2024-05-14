package dev.foundry.crucible.mixin.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.foundry.crucible.extension.PoseStackDuck;
import dev.foundry.crucible.fog.GlobalPoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Unique
    private GuiGraphics crucible$guiGraphics;

    @Unique
    private final PoseStack crucible$renderStack = new PoseStack();
    @Unique
    private final PoseStack crucible$projectionStack = new PoseStack();

    @Redirect(method = "renderZoomed", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    public PoseStack renderZoomedStack() {
        return ((PoseStackDuck) this.crucible$renderStack).reset();
    }

    @Redirect(method = "getProjectionMatrix", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    public PoseStack getProjectionMatrixStack() {
        return ((PoseStackDuck) this.crucible$projectionStack).reset();
    }

    @Redirect(method = "render", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    public PoseStack renderStack() {
        return ((PoseStackDuck) this.crucible$renderStack).reset();
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
    public GuiGraphics create(Minecraft minecraft, MultiBufferSource.BufferSource buffer) {
        if (this.crucible$guiGraphics == null) {
            this.crucible$guiGraphics = new GuiGraphics(minecraft, buffer);
        } else {
            ((PoseStackDuck) this.crucible$guiGraphics.pose()).reset();
        }
        return this.crucible$guiGraphics;
    }
}
