package dev.foundry.crucible.forge;

import dev.foundry.crucible.Crucible;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Crucible.MOD_ID)
public class CrucibleForge {
    public CrucibleForge() {
        Crucible.init();
    }
}