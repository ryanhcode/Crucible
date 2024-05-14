package dev.foundry.crucible.mixin.client;

import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Frustum.class)
public class FrustumMixin {

    @Shadow
    private Vector4f viewVector;

    @Shadow
    private double camX;

    @Shadow
    private double camY;

    @Shadow
    private double camZ;

    /**
     * @author Ocelot
     * @reason Remove while loop
     */
    @Overwrite
    public Frustum offsetToFullyIncludeCameraCube(int i) {
//        this.camX -= this.viewVector.x();
//        this.camY -= this.viewVector.y();
//        this.camZ -= this.viewVector.z();
        return (Frustum) (Object) this;
    }

    @Inject(method = "calculateFrustum", at = @At("TAIL"))
    public void calculateFrustum(Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.viewVector.normalize(4.0F);
    }
}
