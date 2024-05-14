package dev.foundry.crucible.mixin.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.foundry.crucible.extension.PoseStackDuck;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Deque;

@Mixin(PoseStack.class)
public abstract class PoseStackMixin implements PoseStackDuck {

    @Shadow
    @Final
    private Deque<PoseStack.Pose> poseStack;

    @Shadow
    public abstract PoseStack.Pose last();

    @Override
    public void store(PoseStack.Pose pose) {
        PoseStack.Pose last = this.last();
        pose.pose().set(last.pose());
        pose.normal().set(last.normal());
    }

    @Override
    public PoseStack reset() {
        this.poseStack.clear();
        this.poseStack.add(new PoseStack.Pose(new Matrix4f(), new Matrix3f()));
        return (PoseStack) (Object) this;
    }

    @Override
    public Matrix4fc getRawPosition() {
        return this.last().pose();
    }

    @Override
    public Matrix3fc getRawNormal() {
        return this.last().normal();
    }
}
