package dev.foundry.crucible;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Contract;

public class CruciblePlatform {

    @SuppressWarnings("Contract")
    @Contract("_->_")
    @ExpectPlatform
    public static String mapMethod(String owner, String name, String descriptor) {
        throw new AssertionError();
    }
}
