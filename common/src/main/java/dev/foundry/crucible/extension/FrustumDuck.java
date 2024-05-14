package dev.foundry.crucible.extension;

import org.joml.Matrix4f;

public interface FrustumDuck {

    void crucible$reset(Matrix4f modelView, Matrix4f projection);
}
