package dev.foundry.crucible;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Contract;

public class CruciblePlatform {

    @SuppressWarnings("Contract")
    @Contract("_->_")
    @ExpectPlatform
    public static String mapMethod(Class<?> owner, String obfuscatedName, String name, String descriptor) {
        throw new AssertionError();
    }

    @SuppressWarnings("Contract")
    @Contract("_->_")
    @ExpectPlatform
    public static String mapField(Class<?> owner, String obfuscatedName, String name, String descriptor) {
        throw new AssertionError();
    }
}
