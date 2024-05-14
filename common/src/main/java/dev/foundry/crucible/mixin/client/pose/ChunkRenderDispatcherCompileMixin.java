package dev.foundry.crucible.mixin.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.foundry.crucible.fog.GlobalPoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public class ChunkRenderDispatcherCompileMixin {

    @Redirect(method = "compile", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    public PoseStack poseStack() {
        return GlobalPoseStack.getJoml();
    }
}
