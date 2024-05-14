package dev.foundry.crucible.extension;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;

public interface Vec3Duck {

    Vec3 crucible$setX(double x);

    Vec3 crucible$setY(double y);

    Vec3 crucible$setZ(double z);

    Vec3 crucible$set(double x, double y, double z);

    default Vec3 crucible$set(Vec3 vec) {
        return this.crucible$set(vec.x, vec.y, vec.z);
    }

    default Vec3 crucible$set(Vector3dc pos) {
       return this.crucible$set(pos.x(), pos.y(), pos.z());
    }

    default Vec3 crucible$setNormalize(Vec3 vec) {
        double lengthSq = vec.lengthSqr();
        if (lengthSq < 1.0E-8) {
            return Vec3.ZERO;
        }

        double length = Math.sqrt(lengthSq);
        return ((Vec3Duck) vec).crucible$set(vec.x / length, vec.y / length, vec.z / length);
    }
}
