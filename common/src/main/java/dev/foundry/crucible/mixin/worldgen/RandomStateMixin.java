package dev.foundry.crucible.mixin.worldgen;

import dev.foundry.crucible.worldgen.CrucibleWorldGenHook;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RandomState.class, priority = 500)
public class RandomStateMixin {

    @Mutable
    @Shadow
    @Final
    private Climate.Sampler sampler;

    @Mutable
    @Shadow
    @Final
    private NoiseRouter router;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(NoiseGeneratorSettings noiseGeneratorSettings, HolderGetter holderGetter, long l, CallbackInfo ci) {
        this.router = new NoiseRouter(
                CrucibleWorldGenHook.simplify(this.router.barrierNoise()),
                CrucibleWorldGenHook.simplify(this.router.fluidLevelFloodednessNoise()),
                CrucibleWorldGenHook.simplify(this.router.fluidLevelSpreadNoise()),
                CrucibleWorldGenHook.simplify(this.router.lavaNoise()),
                CrucibleWorldGenHook.simplify(this.router.temperature()),
                CrucibleWorldGenHook.simplify(this.router.vegetation()),
                CrucibleWorldGenHook.simplify(this.router.continents()),
                CrucibleWorldGenHook.simplify(this.router.erosion()),
                CrucibleWorldGenHook.simplify(this.router.depth()),
                CrucibleWorldGenHook.simplify(this.router.ridges()),
                CrucibleWorldGenHook.simplify(this.router.initialDensityWithoutJaggedness()),
                CrucibleWorldGenHook.simplify(this.router.finalDensity()),
                CrucibleWorldGenHook.simplify(this.router.veinToggle()),
                CrucibleWorldGenHook.simplify(this.router.veinRidged()),
                CrucibleWorldGenHook.simplify(this.router.veinGap()));
        this.sampler = new Climate.Sampler(
                CrucibleWorldGenHook.simplify(this.sampler.temperature()),
                CrucibleWorldGenHook.simplify(this.sampler.humidity()),
                CrucibleWorldGenHook.simplify(this.sampler.continentalness()),
                CrucibleWorldGenHook.simplify(this.sampler.erosion()),
                CrucibleWorldGenHook.simplify(this.sampler.depth()),
                CrucibleWorldGenHook.simplify(this.sampler.weirdness()),
                this.sampler.spawnTarget());
    }
}
