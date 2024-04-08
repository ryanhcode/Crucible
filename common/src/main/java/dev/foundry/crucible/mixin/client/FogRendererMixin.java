package dev.foundry.crucible.mixin.client;

import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Shadow
    @Final
    private static List<FogRenderer.MobEffectFogFunction> MOB_EFFECT_FOG;

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
}
