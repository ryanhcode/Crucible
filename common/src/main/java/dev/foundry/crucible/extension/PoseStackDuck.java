package dev.foundry.crucible.extension;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;

public interface PoseStackDuck {

    void store(PoseStack.Pose pose);

    PoseStack reset();

    Matrix4fc getRawPosition();

    Matrix3fc getRawNormal();
}
