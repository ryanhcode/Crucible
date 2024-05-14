package dev.foundry.crucible.mixin.client;

import dev.foundry.crucible.extension.FrustumDuck;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Frustum.class)
public abstract class FrustumMixin implements FrustumDuck {

    @Shadow
    private Vector4f viewVector;

    @Shadow
    private double camX;

    @Shadow
    private double camY;

    @Shadow
    private double camZ;

    @Shadow
    protected abstract void calculateFrustum(Matrix4f modelView, Matrix4f projection);

    @Inject(method = "<init>(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", at = @At("TAIL"))
    public void init(Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.viewVector = new Vector4f();
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/culling/Frustum;)V", at = @At("TAIL"))
    public void initFrustum(Frustum frustum, CallbackInfo ci) {
        // Mutability
        this.viewVector = new Vector4f(this.viewVector);
    }

    /**
     * @author Ocelot
     * @reason Remove while loop
     */
    @Overwrite
    public Frustum offsetToFullyIncludeCameraCube(int i) {
        this.camX -= this.viewVector.x();
        this.camY -= this.viewVector.y();
        this.camZ -= this.viewVector.z();
        return (Frustum) (Object) this;
    }

    @Redirect(method = "calculateFrustum", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;transformTranspose(Lorg/joml/Vector4f;)Lorg/joml/Vector4f;"))
    public Vector4f transformTranspose(Matrix4f instance, Vector4f v) {
        return instance.transformTranspose(v).normalize(4.0F);
    }

    @Override
    public void crucible$reset(Matrix4f modelView, Matrix4f projection) {
        this.calculateFrustum(modelView, projection);
    }
}
