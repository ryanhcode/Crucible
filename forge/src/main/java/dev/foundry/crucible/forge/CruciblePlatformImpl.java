package dev.foundry.crucible.forge;

import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class CruciblePlatformImpl {

    public static String mapMethod(Class<?> owner, String obfuscatedName, String name, String descriptor) {
        return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, obfuscatedName);
    }

    public static String mapField(Class<?> owner, String obfuscatedName, String name, String descriptor) {
        return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, obfuscatedName);
    }
}
