package dev.foundry.crucible.forge;

import dev.foundry.crucible.Crucible;
import dev.foundry.crucible.worldgen.CrucibleWorldGenHook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Crucible.MOD_ID)
public class CrucibleForge {

    public CrucibleForge() {
        Crucible.init();
        MinecraftForge.EVENT_BUS.<ServerStoppedEvent>addListener(event -> CrucibleWorldGenHook.clear());
    }
}