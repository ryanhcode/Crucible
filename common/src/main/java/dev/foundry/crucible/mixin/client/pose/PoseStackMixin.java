package dev.foundry.crucible.mixin.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.foundry.crucible.extension.PoseStackDuck;
import net.minecraft.util.Mth;
import org.joml.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PoseStack.class)
public class PoseStackMixin implements PoseStackDuck {

    @Unique
    private final Matrix4fStack crucible$position = new Matrix4fStack(128);
    @Unique
    private final Matrix3fStack crucible$normal = new Matrix3fStack(128);
    @Unique
    private int crucible$layer;

    /**
     * @reason Don't make new matrices after every push
     * @author Ocelot
     */
    @Overwrite
    public void translate(float x, float g, float h) {
        this.crucible$position.translate(x, g, h);
    }

    /**
     * @reason Don't make new matrices after every push
     * @author Ocelot
     */
    @Overwrite
    public void scale(float x, float y, float z) {
        this.crucible$position.scale(x, y, z);
        if (x == y && y == z) {
            if (x > 0.0F) {
                return;
            }

            this.crucible$normal.scale(-1.0F);
        }

        float f = 1.0F / x;
        float f1 = 1.0F / y;
        float f2 = 1.0F / z;
        float f3 = Mth.fastInvCubeRoot(f * f1 * f2);
        this.crucible$normal.scale(f3 * f, f3 * f1, f3 * f2);
    }

    /**
     * @reason Don't make new matrices after every push
     * @author Ocelot
     */
    @Overwrite
    public void mulPose(Quaternionf quaternionf) {
        this.crucible$position.rotate(quaternionf);
        this.crucible$normal.rotate(quaternionf);
    }

    /**
     * @reason Don't make new matrices after every push
     * @author Ocelot
     */
    @Overwrite
    public void rotateAround(Quaternionf quaternionf, float f, float g, float h) {
        this.crucible$position.rotateAround(quaternionf, f, g, h);
        this.crucible$normal.rotate(quaternionf);
    }

    /**
     * @reason Don't make new matrices after every push
     * @author Ocelot
     */
    @Overwrite
    public void pushPose() {
        this.crucible$position.pushMatrix();
        this.crucible$normal.pushMatrix();
        this.crucible$layer++;
    }

    /**
     * @reason Don't make new matrices after every push
     * @author Ocelot
     */
    @Overwrite
    public void popPose() {
        this.crucible$position.popMatrix();
        this.crucible$normal.popMatrix();
        this.crucible$layer--;
    }

    /**
     * @reason Don't make new matrices after every push
     * @author Ocelot
     */
    @Overwrite
    public PoseStack.Pose last() {
        return new PoseStack.Pose(this.crucible$position, this.crucible$normal);
    }

    /**
     * @reason Don't make new matrices after every push
     * @author Ocelot
     */
    @Overwrite
    public boolean clear() {
        return this.crucible$layer == 0;
    }

    /**
     * @reason Don't make new matrices after every push
     * @author Ocelot
     */
    @Overwrite
    public void setIdentity() {
        this.crucible$position.identity();
        this.crucible$normal.identity();
    }

    /**
     * @reason Don't make new matrices after every push
     * @author Ocelot
     */
    @Overwrite
    public void mulPoseMatrix(Matrix4f matrix4f) {
        this.crucible$position.mul(matrix4f);
    }

    @Override
    public PoseStack reset() {
        this.crucible$position.clear();
        this.crucible$normal.clear();
        this.crucible$layer = 0;
        return (PoseStack) (Object) this;
    }
}
