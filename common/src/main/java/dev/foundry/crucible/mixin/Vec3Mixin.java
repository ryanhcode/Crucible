package dev.foundry.crucible.mixin;

import dev.foundry.crucible.extension.Vec3Duck;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3.class)
public class Vec3Mixin implements Vec3Duck {

    @Mutable
    @Shadow
    @Final
    public double x;

    @Mutable
    @Shadow
    @Final
    public double y;

    @Mutable
    @Shadow
    @Final
    public double z;

    @Override
    public Vec3 setX(double x) {
        this.x = x;
        return (Vec3) (Object) this;
    }

    @Override
    public Vec3 setY(double y) {
        this.y = y;
        return (Vec3) (Object) this;
    }

    @Override
    public Vec3 setZ(double z) {
        this.z = z;
        return (Vec3) (Object) this;
    }

    @Override
    public Vec3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return (Vec3) (Object) this;
    }
}
