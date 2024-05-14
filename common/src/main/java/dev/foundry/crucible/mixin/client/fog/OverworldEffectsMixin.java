package dev.foundry.crucible.mixin.client.fog;

import dev.foundry.crucible.extension.Vec3Duck;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DimensionSpecialEffects.OverworldEffects.class)
public class OverworldEffectsMixin {

    @Redirect(method = "getBrightnessDependentFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 multiply(Vec3 instance, double scaleX, double scaleY, double scaleZ) {
        return ((Vec3Duck) instance).crucible$set(instance.x * scaleX, instance.y * scaleY, instance.z * scaleZ);
    }
}
