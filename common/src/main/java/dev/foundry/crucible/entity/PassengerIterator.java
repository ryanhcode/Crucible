package dev.foundry.crucible.entity;

import net.minecraft.world.entity.Entity;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public final class PassengerIterator implements Iterator<Entity> {

    private final Queue<Entity> passengers;

    public PassengerIterator(Entity entity) {
        this.passengers = new ArrayDeque<>();
        this.passengers.add(entity);
    }

    @Override
    public boolean hasNext() {
        return !this.passengers.isEmpty();
    }

    @Override
    public Entity next() {
        Entity entity = this.passengers.poll();
        if (entity != null) {
            this.passengers.addAll(entity.getPassengers());
        }
        return entity;
    }
}
