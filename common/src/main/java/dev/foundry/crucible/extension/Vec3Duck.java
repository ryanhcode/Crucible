package dev.foundry.crucible.extension;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;

public interface Vec3Duck {

    Vec3 setX(double x);

    Vec3 setY(double y);

    Vec3 setZ(double z);

    Vec3 set(double x, double y, double z);

    default Vec3 set(Vec3 vec) {
        return this.set(vec.x, vec.y, vec.z);
    }

    default Vec3 set(Vector3dc pos) {
       return this.set(pos.x(), pos.y(), pos.z());
    }
}
