package dev.foundry.crucible.mixin.client.pipeline;

import com.mojang.blaze3d.platform.NativeImage;
import dev.foundry.crucible.extension.NativeImageDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.renderer.texture.SpriteContents$InterpolationData")
public class SpriteContentsInterpolationDataMixin {

    @Redirect(method = "getPixel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/NativeImage;getPixelRGBA(II)I"))
    public int getPixelRGBA(NativeImage instance, int x, int y) {
        return ((NativeImageDuck) (Object) instance).crucible$getPixelRGBAUnchecked(x, y);
    }
}
