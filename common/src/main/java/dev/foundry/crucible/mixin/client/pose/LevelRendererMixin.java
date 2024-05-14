package dev.foundry.crucible.mixin.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.foundry.crucible.extension.FrustumDuck;
import dev.foundry.crucible.extension.Vec3Duck;
import dev.foundry.crucible.fog.GlobalPoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    @Final
    private static double CEILED_SECTION_DIAGONAL;

    @Unique
    private Frustum crucible$frustum;
    @Unique
    private ThreadLocal<Vec3> crucible$renderChunkPos;
    @Unique
    private ThreadLocal<Vec3> crucible$temp;
    @Unique
    private ThreadLocal<Vec3> crucible$temp2;
    @Unique
    private ThreadLocal<BlockPos.MutableBlockPos> crucible$renderTemp;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Minecraft minecraft, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, RenderBuffers renderBuffers, CallbackInfo ci) {
        this.crucible$renderChunkPos = ThreadLocal.withInitial(() -> new Vec3(0, 0, 0));
        this.crucible$temp = ThreadLocal.withInitial(() -> new Vec3(0, 0, 0));
        this.crucible$temp2 = ThreadLocal.withInitial(() -> new Vec3(0, 0, 0));
        this.crucible$renderTemp = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
    }

    @Redirect(method = "renderLineBox(Lcom/mojang/blaze3d/vertex/VertexConsumer;DDDDDDFFFF)V", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    private static PoseStack poseStack() {
        return GlobalPoseStack.get();
    }

    @Redirect(method = "prepareCullFrustum", at = @At(value = "NEW", target = "(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)Lnet/minecraft/client/renderer/culling/Frustum;"))
    public Frustum frustum(Matrix4f modelView, Matrix4f projection) {
        if (this.crucible$frustum != null) {
            ((FrustumDuck) this.crucible$frustum).crucible$reset(modelView, projection);
        } else {
            this.crucible$frustum = new Frustum(modelView, projection);
        }
        return this.crucible$frustum;
    }

    @Redirect(method = "updateRenderChunks", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 createVector(double x, double y, double z) {
        return ((Vec3Duck) this.crucible$renderChunkPos.get()).crucible$set(x, y, z);
    }

    @Redirect(method = "updateRenderChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", ordinal = 0))
    public Vec3 subtract1(Vec3 instance, Vec3 arg) {
        return ((Vec3Duck) this.crucible$temp.get()).crucible$set(instance.x - arg.x, instance.y - arg.y, instance.z - arg.z);
    }

    @Redirect(method = "updateRenderChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;normalize()Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 normalize(Vec3 instance) {
        return instance;
    }

    @Redirect(method = "updateRenderChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 scale(Vec3 instance, double scale) {
        return instance;
    }

    @Redirect(method = "updateRenderChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", ordinal = 1))
    public Vec3 subtract2(Vec3 instance, Vec3 arg) {
        return ((Vec3Duck) this.crucible$temp2.get()).crucible$set(instance.x - arg.x, instance.y - arg.y, instance.z - arg.z);
    }

    @Redirect(method = "updateRenderChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 add(Vec3 instance, Vec3 arg) {
        double lengthSq = arg.lengthSqr();
        if (lengthSq < 1.0E-8) {
            return instance;
        }

        double length = CEILED_SECTION_DIAGONAL / Math.sqrt(lengthSq);
        return ((Vec3Duck) instance).crucible$set(instance.x + arg.x * length, instance.y + arg.y * length, instance.z + arg.z * length);
    }

    @Redirect(method = "updateRenderChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"))
    public BlockPos containing(double x, double y, double z) {
        return this.crucible$renderTemp.get().set(x, y, z);
    }

    @Redirect(method = "updateRenderChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;", ordinal = 1))
    public BlockPos offset(BlockPos instance, int x, int y, int z) {
        return this.crucible$renderTemp.get().setWithOffset(instance, x, y, z);
    }
}
