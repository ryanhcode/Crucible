package dev.foundry.crucible.mixin.client.blaze3d;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * This mixin aims to reduce the number of redundant assertions and general logic that slows down every render call.
 */
@Mixin(value = GlStateManager.class, remap = false)
public abstract class GLStateManagerMixin {

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void _glBindVertexArray(int array) {
        GL30C.glBindVertexArray(array);
    }
}
