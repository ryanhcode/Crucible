package dev.foundry.crucible.mixin.client.pipeline;

import com.mojang.blaze3d.platform.NativeImage;
import dev.foundry.crucible.extension.NativeImageDuck;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NativeImage.class)
public class NativeImageMixin implements NativeImageDuck {

    @Shadow
    @Final
    private int width;

    @Shadow
    private long pixels;

    @Override
    public int crucible$getPixelRGBAUnchecked(int x, int y) {
        return MemoryUtil.memGetInt(this.pixels + ((long) x + (long) y * (long) this.width) * 4L);
    }
}
