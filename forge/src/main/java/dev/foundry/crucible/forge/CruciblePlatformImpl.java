package dev.foundry.crucible.forge;

import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class CruciblePlatformImpl {

    public static String mapMethod(String owner, String name, String descriptor) {
        return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, name);
    }
}
