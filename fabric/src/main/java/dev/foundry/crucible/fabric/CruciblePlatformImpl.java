package dev.foundry.crucible.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class CruciblePlatformImpl {

    public static String mapMethod(Class<?> owner, String obfuscatedName, String name, String descriptor) {
        return FabricLoader.getInstance().getMappingResolver().mapMethodName("named", owner.getTypeName(), name, descriptor);
    }

    public static String mapField(Class<?> owner, String obfuscatedName, String name, String descriptor) {
        return FabricLoader.getInstance().getMappingResolver().mapFieldName("named", owner.getTypeName(), name, descriptor);
    }
}
