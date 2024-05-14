package dev.foundry.crucible.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.foundry.crucible.extension.PoseStackDuck;
import net.minecraft.util.Mth;
import org.joml.*;

public class JomlPoseStack extends PoseStack implements PoseStackDuck {

    private final Matrix4fStack stack = new Matrix4fStack(128);
    private final Matrix3fStack normal = new Matrix3fStack(128);
    private final Pose last = new Pose(this.stack, this.normal);
    private int layer;

    @Override
    public void translate(double d, double e, double f) {
        this.translate((float)d, (float)e, (float)f);
    }

    @Override
    public void translate(float x, float y, float z) {
        this.stack.translate(x, y, z);
    }

    @Override
    public void scale(float f, float g, float h) {
        this.stack.scale(f, g, h);
        if (f == g && g == h) {
            if (f > 0.0F) {
                return;
            }

            this.normal.scale(-1.0F);
        }

        float i = 1.0F / f;
        float j = 1.0F / g;
        float k = 1.0F / h;
        float l = Mth.fastInvCubeRoot(i * j * k);
        this.normal.scale(l * i, l * j, l * k);
        this.stack.scale(f, g, h);
    }

    @Override
    public void mulPose(Quaternionf quat) {
        this.stack.rotate(quat);
        this.normal.rotate(quat);
    }

    @Override
    public void rotateAround(Quaternionf quat, float ox, float oy, float oz) {
        this.stack.rotateAround(quat, ox, oy, oz);
        this.normal.rotate(quat);
    }

    @Override
    public void pushPose() {
        this.stack.pushMatrix();
        this.normal.pushMatrix();
        this.layer++;
    }

    @Override
    public void popPose() {
        this.stack.popMatrix();
        this.normal.popMatrix();
        this.layer--;
    }

    @Override
    public Pose last() {
        return this.last;
    }

    @Override
    public boolean clear() {
        return this.layer == 0;
    }

    @Override
    public void setIdentity() {
        this.stack.identity();
        this.normal.identity();
    }

    @Override
    public void mulPoseMatrix(Matrix4f matrix4f) {
        this.stack.mul(matrix4f);
    }

    @Override
    public void store(Pose pose) {
        pose.pose().set(this.stack);
        pose.normal().set(this.normal);
    }

    @Override
    public JomlPoseStack reset() {
        this.stack.clear();
        this.normal.clear();
        this.layer = 0;
        return this;
    }

    @Override
    public Matrix4fc getRawPosition() {
        return this.stack;
    }

    @Override
    public Matrix3fc getRawNormal() {
        return this.normal;
    }
}
