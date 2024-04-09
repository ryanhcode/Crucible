package dev.foundry.crucible.mixin.worldgen;

import dev.foundry.crucible.extension.NoiseChunkDuck;
import dev.foundry.crucible.worldgen.CrucibleWorldGenHook;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;
import java.util.function.Predicate;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

    @Unique
    private int crucible$x;
    @Unique
    private int crucible$z;

    @ModifyArg(method = "createNoiseChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseChunk;forChunk(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/DensityFunctions$BeardifierOrMarker;Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/world/level/levelgen/Aquifer$FluidPicker;Lnet/minecraft/world/level/levelgen/blending/Blender;)Lnet/minecraft/world/level/levelgen/NoiseChunk;"), index = 3)
    public NoiseGeneratorSettings editSettings(NoiseGeneratorSettings settings) {
        return new NoiseGeneratorSettings(
                settings.noiseSettings(),
                settings.defaultBlock(),
                settings.defaultFluid(),
                CrucibleWorldGenHook.simplify(settings.noiseRouter()),
                settings.surfaceRule(),
                settings.spawnTarget(),
                settings.seaLevel(),
                settings.disableMobGeneration(),
                settings.aquifersEnabled(),
                settings.oreVeinsEnabled(),
                settings.useLegacyRandomSource());
    }

    @Inject(method = "iterateNoiseColumn", at = @At("HEAD"))
    public void captureXZ(LevelHeightAccessor levelHeightAccessor, RandomState randomState, int x, int z, MutableObject<NoiseColumn> mutableObject, Predicate<BlockState> predicate, CallbackInfoReturnable<OptionalInt> cir) {
        this.crucible$x = x;
        this.crucible$z = z;
    }

    @Redirect(method = "iterateNoiseColumn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseChunk;updateForY(ID)V"))
    public void updateNoise(NoiseChunk instance, int y, double noise) {
        ((NoiseChunkDuck) instance).crucible$updateNoise(this.crucible$x, y, this.crucible$z, noise);
    }

    @Redirect(method = "iterateNoiseColumn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseChunk;updateForX(ID)V"))
    public void cancelUpdateX(NoiseChunk instance, int i, double d) {
    }

    @Redirect(method = "iterateNoiseColumn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseChunk;updateForZ(ID)V"))
    public void cancelUpdateZ(NoiseChunk instance, int i, double d) {
    }
}
