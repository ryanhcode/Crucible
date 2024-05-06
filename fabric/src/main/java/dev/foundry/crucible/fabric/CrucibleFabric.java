package dev.foundry.crucible.fabric;

import dev.foundry.crucible.Crucible;
import dev.foundry.crucible.worldgen.CrucibleWorldGenHook;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class CrucibleFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Crucible.init();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> CrucibleWorldGenHook.init(server.registryAccess()));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> CrucibleWorldGenHook.clear());
    }
}