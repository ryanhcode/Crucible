package dev.foundry.crucible.fabric;

import dev.foundry.crucible.Crucible;
import net.fabricmc.api.ModInitializer;

public class CrucibleFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Crucible.init();
    }
}