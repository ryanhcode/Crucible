package dev.foundry.crucible.mixin.chunk;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(Biome.class)
public abstract class BiomeMixin {

    @Shadow
    @Final
    private BiomeSpecialEffects specialEffects;

    @Shadow
    protected abstract int getGrassColorFromTexture();

    @Shadow
    protected abstract int getFoliageColorFromTexture();

    /**
     * @author Ocelot
     * @reason Get rid of Integer allocations
     */
    @SuppressWarnings("OptionalIsPresent")
    @Overwrite
    public int getGrassColor(double x, double z) {
        Optional<Integer> grassColorOverride = this.specialEffects.getGrassColorOverride();
        return this.specialEffects.getGrassColorModifier().modifyColor(x, z, grassColorOverride.isPresent() ? grassColorOverride.get() : this.getGrassColorFromTexture());
    }

    /**
     * @author Ocelot
     * @reason Get rid of Integer allocations
     */
    @SuppressWarnings("OptionalIsPresent")
    @Overwrite
    public int getFoliageColor() {
        Optional<Integer> foliageColorOverride = this.specialEffects.getFoliageColorOverride();
        return foliageColorOverride.isPresent() ? foliageColorOverride.get() : this.getFoliageColorFromTexture();
    }
}
