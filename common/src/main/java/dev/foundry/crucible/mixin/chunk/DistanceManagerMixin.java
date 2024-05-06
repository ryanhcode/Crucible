package dev.foundry.crucible.mixin.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.Ticket;
import net.minecraft.util.SortedArraySet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;

@Mixin(DistanceManager.class)
public class DistanceManagerMixin {

    @Shadow
    @Final
    Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets;

    @Unique
    private Long2ObjectMap<SortedArraySet<Ticket<?>>> crucible$asyncTickets;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Executor executor, Executor executor2, CallbackInfo ci) {
        this.crucible$asyncTickets = Long2ObjectMaps.synchronize(this.tickets);
    }

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap$FastEntrySet;fastIterator()Lit/unimi/dsi/fastutil/objects/ObjectIterator;"))
    public ObjectIterator<Long2ObjectMap.Entry<SortedArraySet<Ticket<?>>>> purgeStaleTickets(Long2ObjectMap.FastEntrySet<SortedArraySet<Ticket<?>>> instance) {
        return this.tickets.long2ObjectEntrySet().fastIterator();
    }

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;remove(J)Ljava/lang/Object;"))
    public Object removeTicket(Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> instance, long key) {
        return this.crucible$asyncTickets.remove(key);
    }

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;computeIfAbsent(JLit/unimi/dsi/fastutil/longs/Long2ObjectFunction;)Ljava/lang/Object;"))
    public Object getTickets(Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> instance, long key, Long2ObjectFunction<SortedArraySet<Ticket<?>>> mappingFunction) {
        return this.crucible$asyncTickets.computeIfAbsent(key, mappingFunction);
    }

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;get(J)Ljava/lang/Object;"))
    public Object getTickets(Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> instance, long key) {
        return this.crucible$asyncTickets.get(key);
    }

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;isEmpty()Z"))
    public boolean isEmpty(Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> instance) {
        return this.crucible$asyncTickets.isEmpty();
    }
}
