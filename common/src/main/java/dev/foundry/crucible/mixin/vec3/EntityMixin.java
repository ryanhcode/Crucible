package dev.foundry.crucible.mixin.vec3;

import dev.foundry.crucible.extension.Vec3Duck;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {

    @Redirect(method = "updateFluidHeightAndDoFluidPushing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 updateFluidHeightAndDoFluidPushingAdd(Vec3 instance, Vec3 arg) {
        return instance == Vec3.ZERO ? instance.add(arg) : ((Vec3Duck) instance).set(instance.x + arg.x, instance.y + arg.y, instance.z + arg.z);
    }

    @Redirect(method = "updateFluidHeightAndDoFluidPushing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 updateFluidHeightAndDoFluidPushingScale(Vec3 instance, double scale) {
        return ((Vec3Duck) instance).set(instance.x * scale, instance.y * scale, instance.z * scale);
    }

    @Redirect(method = "updateFluidHeightAndDoFluidPushing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;normalize()Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 updateFluidHeightAndDoFluidPushingNormalize(Vec3 instance) {
        double lengthSq = instance.lengthSqr();
        if (lengthSq < 1.0E-8) {
            return Vec3.ZERO;
        }

        double length = Math.sqrt(lengthSq);
        return ((Vec3Duck) instance).set(instance.x / length, instance.y / length, instance.z / length);
    }

    @Redirect(method = "moveRelative", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 moveRelativeAdd(Vec3 instance, Vec3 arg) {
        return instance == Vec3.ZERO ? instance.add(arg) : ((Vec3Duck) instance).set(instance.x + arg.x, instance.y + arg.y, instance.z + arg.z);
    }
}
