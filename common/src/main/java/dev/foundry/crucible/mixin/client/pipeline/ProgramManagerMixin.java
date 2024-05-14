package dev.foundry.crucible.mixin.client.pipeline;

import com.mojang.blaze3d.shaders.ProgramManager;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ProgramManager.class)
public class ProgramManagerMixin {

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUseProgram(int program) {
        GL20C.glUseProgram(program);
    }
}
