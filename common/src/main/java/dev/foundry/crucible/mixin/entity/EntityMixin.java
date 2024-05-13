package dev.foundry.crucible.mixin.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Unique
    private static final ThreadLocal<ObjectList<VoxelShape>> SHAPE_CACHE = ThreadLocal.withInitial(ObjectArrayList::new);

    @Shadow
    private static Vec3 collideWithShapes(Vec3 arg, AABB arg2, List<VoxelShape> list) {
        return null;
    }

    /**
     * @author Ocelot
     * @reason Improve memory usage and speed
     */
    @Overwrite
    public static Vec3 collideBoundingBox(@Nullable Entity entity, Vec3 delta, AABB boundingBox, Level level, List<VoxelShape> list) {
        ObjectList<VoxelShape> shapes = SHAPE_CACHE.get();
        shapes.clear();
        shapes.addAll(list);

        WorldBorder worldborder = level.getWorldBorder();
        AABB expandedBox = boundingBox.expandTowards(delta);
        if (entity != null && worldborder.isInsideCloseToBorder(entity, expandedBox)) {
            shapes.add(worldborder.getCollisionShape());
        }

        Iterable<VoxelShape> blockCollisions = level.getBlockCollisions(entity, expandedBox);
        for (VoxelShape shape : blockCollisions) {
            shapes.add(shape);
        }
        return collideWithShapes(delta, boundingBox, shapes);
    }
}
