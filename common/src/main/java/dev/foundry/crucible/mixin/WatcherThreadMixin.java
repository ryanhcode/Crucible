package dev.foundry.crucible.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "com.electronwill.nightconfig.core.file.FileWatcher.WatcherThread")
public class WatcherThreadMixin {

    @ModifyArg(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/locks/LockSupport;parkNanos(J)V"), index = 0)
    private long park(long nanos) {
        return 1_000_000_000;
    }
}