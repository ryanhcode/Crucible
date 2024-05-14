package dev.foundry.crucible.mixin.client.fog;

import dev.foundry.crucible.extension.Vec3Duck;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DimensionSpecialEffects.EndEffects.class)
public class EndEffectsMixin {

    @Redirect(method = "getBrightnessDependentFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 scale(Vec3 instance, double scale) {
        return ((Vec3Duck) instance).crucible$set(instance.x * scale, instance.y * scale, instance.z * scale);
    }
}
