package dev.foundry.crucible.mixin;

import dev.foundry.crucible.extension.SectionPosDuck;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SectionPos.class)
public class SectionPosMixin extends Vec3i implements SectionPosDuck {

    public SectionPosMixin(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    public SectionPos crucible$setX(int x) {
        this.setX(x);
        return (SectionPos) (Object) this;
    }

    @Override
    public SectionPos crucible$setY(int y) {
        this.setY(y);
        return (SectionPos) (Object) this;
    }

    @Override
    public SectionPos crucible$setZ(int z) {
        this.setZ(z);
        return (SectionPos) (Object) this;
    }

    @Override
    public SectionPos crucible$set(int x, int y, int z) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
        return (SectionPos) (Object) this;
    }
}
