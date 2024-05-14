package dev.foundry.crucible.mixin;

import dev.foundry.crucible.extension.LevelDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Unique
    private final BlockPos.MutableBlockPos crucible$randomPos = new BlockPos.MutableBlockPos();

    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBlockRandomPos(IIII)Lnet/minecraft/core/BlockPos;"))
    public BlockPos getBlockRandomPos(ServerLevel instance, int x, int y, int z, int i) {
        return ((LevelDuck) instance).crucible$getBlockRandomPos(x, y, z, i, this.crucible$randomPos);
    }
}
