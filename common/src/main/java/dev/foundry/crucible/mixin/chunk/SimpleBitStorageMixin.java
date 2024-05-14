package dev.foundry.crucible.mixin.chunk;

import net.minecraft.util.SimpleBitStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleBitStorage.class)
public class SimpleBitStorageMixin {

    @Shadow
    @Final
    private int divideMul;

    @Shadow
    @Final
    private int divideAdd;

    @Shadow
    @Final
    private int divideShift;

    /**
     * @author Ocelot
     * @reason Inline function
     */
    @Overwrite
    private int cellIndex(int i) {
        return (int) ((long) i * ((long) this.divideMul & 0xffffffffL) + ((long) this.divideAdd & 0xffffffffL) >> 32 >> this.divideShift);
    }
}
