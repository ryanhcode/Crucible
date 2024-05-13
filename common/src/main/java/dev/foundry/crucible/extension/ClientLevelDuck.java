package dev.foundry.crucible.extension;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public interface ClientLevelDuck {

    Vector3d getSkyColor(Vec3 arg, float partialTicks, Vector3d store);
}
