package dev.foundry.crucible.mixin.worldgen;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MaterialRuleList.class)
public class MaterialRuleListMixin {

    @Unique
    private NoiseChunk.BlockStateFiller[] crucible$fillers;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(List<NoiseChunk.BlockStateFiller> list, CallbackInfo ci) {
        this.crucible$fillers = list.toArray(new NoiseChunk.BlockStateFiller[0]);
    }

    /**
     * @author Ocelot
     * @reason Use direct field access instead of iterators
     */
    @Overwrite
    public BlockState calculate(DensityFunction.FunctionContext functionContext) {
        for (NoiseChunk.BlockStateFiller filler : this.crucible$fillers) {
            BlockState state = filler.calculate(functionContext);
            if (state != null) {
                return state;
            }
        }

        return null;
    }
}
