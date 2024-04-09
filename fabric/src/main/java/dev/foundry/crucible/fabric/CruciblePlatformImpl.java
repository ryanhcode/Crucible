package dev.foundry.crucible.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class CruciblePlatformImpl {

    public static String mapMethod(String owner, String name, String descriptor) {
        System.out.println(FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace());
        return FabricLoader.getInstance().getMappingResolver().mapMethodName("test", owner, name, descriptor);
    }
}
