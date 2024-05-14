package dev.foundry.crucible.fabric.mixin.fabric;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.function.Supplier;

@Pseudo
@Mixin(value = WeightedBakedModel.class, priority = 2000)
public class WeightedBakedModelMixin {

    @Shadow
    @Final
    private List<WeightedEntry.Wrapper<BakedModel>> list;

    @Shadow
    @Final
    private int totalWeight;

    /**
     * @author Ocelot
     * @reason Memory optimization
     */
    @SuppressWarnings("MixinAnnotationTarget")
    @Overwrite
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        if (!this.list.isEmpty()) {
            WeightedRandom.getWeightedItem(this.list, Math.abs((int) randomSupplier.get().nextLong()) % this.totalWeight).ifPresent(selected -> selected.getData().emitBlockQuads(blockView, state, pos, randomSupplier, context));
        }
    }
}
