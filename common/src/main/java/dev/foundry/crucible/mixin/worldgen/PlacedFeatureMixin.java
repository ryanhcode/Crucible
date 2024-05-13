package dev.foundry.crucible.mixin.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.stream.Stream;

@Mixin(PlacedFeature.class)
public class PlacedFeatureMixin {

    @Shadow
    @Final
    private List<PlacementModifier> placement;

    @Shadow
    @Final
    private Holder<ConfiguredFeature<?, ?>> feature;

    /**
     * @reason Remove streams
     * @author Ocelot
     */
    @Overwrite
    private boolean placeWithContext(PlacementContext arg, RandomSource arg2, BlockPos arg3) {
        Stream<BlockPos> stream = Stream.of(arg3);
        for (PlacementModifier placementModifier : this.placement) {
            stream = stream.flatMap(arg4 -> placementModifier.getPositions(arg, arg2, arg4));
        }
        List<BlockPos> positions = stream.toList();

        ConfiguredFeature<?, ?> configuredFeature = this.feature.value();
        boolean placed = false;
        for (BlockPos pos : positions) {
            if (configuredFeature.place(arg.getLevel(), arg.generator(), arg2, pos)) {
                placed = true;
            }
        }
        return placed;
    }
}
