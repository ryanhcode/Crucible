package dev.foundry.crucible.mixin.network;

import dev.foundry.crucible.extension.SectionPosDuck;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @Unique
    private final SectionPos crucible$moveCache = SectionPos.of(0L);

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;of(Lnet/minecraft/world/level/entity/EntityAccess;)Lnet/minecraft/core/SectionPos;"))
    public SectionPos of(EntityAccess entity) {
        return ((SectionPosDuck) this.crucible$moveCache).crucible$set(entity);
    }
}
