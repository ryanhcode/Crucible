package dev.foundry.crucible.mixin.client.fog;

import dev.foundry.crucible.extension.ClientLevelDuck;
import dev.foundry.crucible.extension.Vec3Duck;
import dev.foundry.crucible.fog.JomlCubicSampler;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Shadow
    @Final
    private static List<FogRenderer.MobEffectFogFunction> MOB_EFFECT_FOG;

    @Unique
    private static final Vector3d crucible$tempVec = new Vector3d();
    @Unique
    private static final Vec3 crucible$tempColor = new Vec3(0, 0, 0);

    @Unique
    private static ClientLevel crucible$captureLevel;
    @Unique
    private static float crucible$capturePartialTicks;

    /**
     * @author Ocelot
     * @reason This removes the stream
     */
    @Overwrite
    public static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity entity, float partialTicks) {
        if (entity instanceof LivingEntity livingEntity) {
            for (FogRenderer.MobEffectFogFunction function : MOB_EFFECT_FOG) {
                if (function.isEnabled(livingEntity, partialTicks)) {
                    return function;
                }
            }
        }
        return null;
    }

    @Inject(method = "setupColor", at = @At("HEAD"))
    private static void setupColor(Camera camera, float partialTicks, ClientLevel level, int i, float g, CallbackInfo ci) {
        crucible$captureLevel = level;
        crucible$capturePartialTicks = partialTicks;
    }

    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;subtract(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 subtract(Vec3 instance, double x, double y, double z) {
        return instance;
    }

    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 scale(Vec3 instance, double scale) {
        return instance;
    }

    // Epic hack to not create 10 million vectors
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 gaussianSampleVec3(Vec3 pos, CubicSampler.Vec3Fetcher fetcher) {
        float time = Mth.clamp(Mth.cos(crucible$captureLevel.getTimeOfDay(crucible$capturePartialTicks) * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
        JomlCubicSampler.gaussianSampleVec3(crucible$tempVec.sub(2.0, 2.0, 2.0).mul(0.25), (x, y, z, store) -> {
            int color = crucible$captureLevel.getBiomeManager().getNoiseBiomeAtQuart(x, y, z).value().getFogColor();
            double r = (double) (color >> 16 & 0xFF) / 255.0;
            double g = (double) (color >> 8 & 0xFF) / 255.0;
            double b = (double) (color & 0xFF) / 255.0;
            Vec3 fogColor = crucible$captureLevel.effects().getBrightnessDependentFogColor(((Vec3Duck) crucible$tempColor).set(r, g, b), time);
            store.set(fogColor.x, fogColor.y, fogColor.z);
        });
        return ((Vec3Duck) crucible$tempColor).set(crucible$tempVec);
    }

    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 getSkyColor(ClientLevel instance, Vec3 pos, float partialTicks) {
        ((ClientLevelDuck) instance).getSkyColor(pos, partialTicks, crucible$tempVec);
        return ((Vec3Duck) crucible$tempColor).set(crucible$tempVec);
    }
}
