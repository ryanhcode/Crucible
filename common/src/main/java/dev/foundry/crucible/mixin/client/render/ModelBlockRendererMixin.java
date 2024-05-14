package dev.foundry.crucible.mixin.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.foundry.crucible.extension.PoseStackDuck;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {

    @Unique
    private static final ThreadLocal<PoseStack.Pose> crucible$POSE = ThreadLocal.withInitial(() -> new PoseStack.Pose(new Matrix4f(), new Matrix3f()));

    @Inject(method = "tesselateWithAO", at = @At("HEAD"))
    public void tesselateWithAOPose(BlockAndTintGetter blockAndTintGetter, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean bl, RandomSource randomSource, long l, int i, CallbackInfo ci) {
        ((PoseStackDuck) poseStack).store(crucible$POSE.get());
    }

    @Inject(method = "tesselateWithoutAO", at = @At("HEAD"))
    public void tesselateWithoutAOPose(BlockAndTintGetter blockAndTintGetter, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean bl, RandomSource randomSource, long l, int i, CallbackInfo ci) {
        ((PoseStackDuck) poseStack).store(crucible$POSE.get());
    }

    @Redirect(method = "renderModelFaceFlat", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;last()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;"))
    public PoseStack.Pose renderModelFaceFlatLast(PoseStack instance) {
        return crucible$POSE.get();
    }

    @Redirect(method = "renderModelFaceAO", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;last()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;"))
    public PoseStack.Pose renderModelFaceAOLast(PoseStack instance) {
        return crucible$POSE.get();
    }
}
