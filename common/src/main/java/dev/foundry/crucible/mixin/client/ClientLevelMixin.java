package dev.foundry.crucible.mixin.client;

import dev.foundry.crucible.extension.ClientLevelDuck;
import dev.foundry.crucible.fog.JomlCubicSampler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements ClientLevelDuck {

    @Shadow
    public abstract int getSkyFlashTime();

    @Unique
    private final Vector3d crucible$tempVec = new Vector3d();

    protected ClientLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Override
    public Vector3d getSkyColor(Vec3 pos, float partialTicks, Vector3d value) {
        JomlCubicSampler.gaussianSampleVec3(this.crucible$tempVec.sub(2.0, 2.0, 2.0).mul(0.25), (x, y, z, store) -> {
            int color = this.getBiomeManager().getNoiseBiomeAtQuart(x, y, z).value().getSkyColor();
            double r = (double) (color >> 16 & 0xFF) / 255.0;
            double g = (double) (color >> 8 & 0xFF) / 255.0;
            double b = (double) (color & 0xFF) / 255.0;
            store.set(r, g, b);
        });

        float f = this.getTimeOfDay(partialTicks);
        float f1 = Mth.cos(f * (float) (Math.PI * 2)) * 2.0F + 0.5F;
        f1 = Mth.clamp(f1, 0.0F, 1.0F);
        float r = (float) this.crucible$tempVec.x * f1;
        float g = (float) this.crucible$tempVec.y * f1;
        float b = (float) this.crucible$tempVec.z * f1;
        float f5 = this.getRainLevel(partialTicks);
        if (f5 > 0.0F) {
            float f6 = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.6F;
            float f7 = 1.0F - f5 * 0.75F;
            r = r * f7 + f6 * (1.0F - f7);
            g = g * f7 + f6 * (1.0F - f7);
            b = b * f7 + f6 * (1.0F - f7);
        }

        float f9 = this.getThunderLevel(partialTicks);
        if (f9 > 0.0F) {
            float f10 = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.2F;
            float f8 = 1.0F - f9 * 0.75F;
            r = r * f8 + f10 * (1.0F - f8);
            g = g * f8 + f10 * (1.0F - f8);
            b = b * f8 + f10 * (1.0F - f8);
        }

        int i = this.getSkyFlashTime();
        if (i > 0) {
            float f11 = (float) i - partialTicks;
            if (f11 > 1.0F) {
                f11 = 1.0F;
            }

            f11 *= 0.45F;
            r = r * (1.0F - f11) + 0.8F * f11;
            g = g * (1.0F - f11) + 0.8F * f11;
            b = b * (1.0F - f11) + f11;
        }

        return value.set(r, g, b);
    }
}
