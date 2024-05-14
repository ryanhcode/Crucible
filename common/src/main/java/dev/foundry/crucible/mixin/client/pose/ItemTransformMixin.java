package dev.foundry.crucible.mixin.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransform;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemTransform.class)
public class ItemTransformMixin {

    @Shadow
    @Final
    public static ItemTransform NO_TRANSFORM;
    @Unique
    private final Matrix4f facade$transform = new Matrix4f();
    @Unique
    private final Matrix4f facade$transformMirror = new Matrix4f();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void updateTransform(Vector3f rotation, Vector3f translation, Vector3f scale, CallbackInfo ci) {
        float degToRad = (float) (Math.PI / 180.0);

        this.facade$transform.translate(translation.x(), translation.y(), translation.z());
        this.facade$transform.rotateXYZ(rotation.x() * degToRad, rotation.y() * degToRad, rotation.z() * degToRad);
        this.facade$transform.scale(scale.x(), scale.y(), scale.z());

        this.facade$transformMirror.translate(-translation.x(), translation.y(), translation.z());
        this.facade$transformMirror.rotateXYZ(rotation.x() * degToRad, -rotation.y() * degToRad, -rotation.z() * degToRad);
        this.facade$transformMirror.scale(scale.x(), scale.y(), scale.z());
    }

    /**
     * @author Ocelot
     * @reason Use a single fast matrix transform instead 3 separate transforms
     */
    @Overwrite
    public void apply(boolean mirror, PoseStack matrixStack) {
        if ((Object) this == NO_TRANSFORM) {
            return;
        }

        Matrix4f pose = matrixStack.last().pose();
        Matrix4f transform = mirror ? this.facade$transformMirror : this.facade$transform;

        // The transform is guaranteed to be affine, so we can skip the checks for that
        if ((pose.properties() & Matrix4fc.PROPERTY_IDENTITY) != 0) {
            pose.set(transform);
        } else if ((pose.properties() & Matrix4fc.PROPERTY_AFFINE) != 0) {
            pose.mulAffine(transform, pose);
        } else {
            pose.mulAffineR(transform, pose);
        }
    }
}
