package dev.foundry.crucible.mixin.client.blaze3d;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * This mixin aims to reduce the number of redundant assertions and general logic that slows down every render call.
 */
@Mixin(value = RenderSystem.class, remap = false)
public abstract class RenderSystemMixin {

    @Shadow
    public static void assertOnRenderThread() {
    }

    @Shadow
    private static void _setShaderColor(float f, float g, float h, float i) {
    }

    @Shadow
    public static boolean isOnRenderThread() {
        return false;
    }

    @Shadow
    public static void recordRenderCall(RenderCall arg) {
    }

    @Shadow
    public static void setShaderTexture(int i, int j) {
    }

    @Shadow
    private static float shaderLineWidth;

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void disableDepthTest() {
        GlStateManager._disableDepthTest();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void enableDepthTest() {
        GlStateManager._enableDepthTest();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void enableScissor(int i, int j, int k, int l) {
        GlStateManager._enableScissorTest();
        GlStateManager._scissorBox(i, j, k, l);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void disableScissor() {
        GlStateManager._disableScissorTest();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void depthFunc(int i) {
        GlStateManager._depthFunc(i);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void depthMask(boolean bl) {
        GlStateManager._depthMask(bl);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void enableBlend() {
        GlStateManager._enableBlend();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void disableBlend() {
        GlStateManager._disableBlend();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void blendFunc(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor) {
        GlStateManager._blendFunc(sourceFactor.value, destFactor.value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void blendFunc(int i, int j) {
        GlStateManager._blendFunc(i, j);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void blendFuncSeparate(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor, GlStateManager.SourceFactor sourceFactor2, GlStateManager.DestFactor destFactor2) {
        GlStateManager._blendFuncSeparate(sourceFactor.value, destFactor.value, sourceFactor2.value, destFactor2.value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void blendFuncSeparate(int i, int j, int k, int l) {
        GlStateManager._blendFuncSeparate(i, j, k, l);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void blendEquation(int i) {
        GlStateManager._blendEquation(i);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void enableCull() {
        GlStateManager._enableCull();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void disableCull() {
        GlStateManager._disableCull();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void polygonMode(int face, int mode) {
        GL11C.glPolygonMode(face, mode);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void enablePolygonOffset() {
        GlStateManager._enablePolygonOffset();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void disablePolygonOffset() {
        GlStateManager._disablePolygonOffset();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void polygonOffset(float f, float g) {
        GlStateManager._polygonOffset(f, g);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void enableColorLogicOp() {
        GlStateManager._enableColorLogicOp();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void disableColorLogicOp() {
        GlStateManager._disableColorLogicOp();
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void logicOp(GlStateManager.LogicOp logicOp) {
        GlStateManager._logicOp(logicOp.value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void activeTexture(int i) {
        GlStateManager._activeTexture(i);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void texParameter(int target, int pname, int param) {
        GL11C.glTexParameteri(target, pname, param);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void deleteTexture(int i) {
        GL11C.glDeleteTextures(i);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void bindTexture(int i) {
        GlStateManager._bindTexture(i);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void viewport(int i, int j, int k, int l) {
        GlStateManager._viewport(i, j, k, l);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        GlStateManager._colorMask(bl, bl2, bl3, bl4);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void stencilFunc(int i, int j, int k) {
        GlStateManager._stencilFunc(i, j, k);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void stencilMask(int i) {
        GlStateManager._stencilMask(i);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void stencilOp(int i, int j, int k) {
        GlStateManager._stencilOp(i, j, k);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void clearDepth(double depth) {
        GL11C.glClearDepth(depth);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void clearColor(float red, float green, float blue, float alpha) {
        GL11C.glClearColor(red, green, blue, alpha);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void clearStencil(int s) {
        GL11C.glClearStencil(s);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void clear(int mask, boolean checkError) {
        GL11C.glClear(mask);
        if (checkError) {
            GL11C.glGetError();
        }
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void setShaderColor(float red, float green, float blue, float alpha) {
        assertOnRenderThread();
        _setShaderColor(red, green, blue, alpha);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void drawElements(int mode, int count, int type) {
        GL11C.glDrawElements(mode, count, type, 0L);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void lineWidth(float value) {
        assertOnRenderThread();
        shaderLineWidth = value;
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void pixelStore(int pname, int param) {
        GL11C.glPixelStorei(pname, param);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
        GL11C.glReadPixels(x, y, width, height, format, type, pixels);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void getString(int name, Consumer<String> consumer) {
        consumer.accept(GL11C.glGetString(name));
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static String getBackendDescription() {
        return String.format(Locale.ROOT, "LWJGL version %s", GLX._getLWJGLVersion());
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void setErrorCallback(GLFWErrorCallbackI gLFWErrorCallbackI) {
        GLX._setGlfwErrorCallback(gLFWErrorCallbackI);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void renderCrosshair(int length) {
        GLX._renderCrosshair(length, true, true, true);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glBindBuffer(int target, IntSupplier buffer) {
        GL15C.glBindBuffer(target, buffer.getAsInt());
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glBindVertexArray(Supplier<Integer> array) {
        GL30C.glBindVertexArray(array.get());
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glBufferData(int target, ByteBuffer data, int usage) {
        GL15C.glBufferData(target, data, usage);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glDeleteBuffers(int buffer) {
        GL15C.glDeleteBuffers(buffer);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glDeleteVertexArrays(int array) {
        GL30C.glDeleteVertexArrays(array);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniform1i(int location, int v0) {
        GL20C.glUniform1i(location, v0);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniform1(int location, IntBuffer value) {
        GL20C.glUniform1iv(location, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniform2(int location, IntBuffer value) {
        GL20C.glUniform2iv(location, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniform3(int location, IntBuffer value) {
        GL20C.glUniform3iv(location, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniform4(int location, IntBuffer value) {
        GL20C.glUniform4iv(location, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniform1(int location, FloatBuffer value) {
        GL20C.glUniform1fv(location, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniform2(int location, FloatBuffer value) {
        GL20C.glUniform2fv(location, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniform3(int location, FloatBuffer value) {
        GL20C.glUniform3fv(location, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniform4(int location, FloatBuffer value) {
        GL20C.glUniform4fv(location, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniformMatrix2(int location, boolean transpose, FloatBuffer value) {
        GL20C.glUniformMatrix2fv(location, transpose, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniformMatrix3(int location, boolean transpose, FloatBuffer value) {
        GL20C.glUniformMatrix3fv(location, transpose, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glUniformMatrix4(int location, boolean transpose, FloatBuffer value) {
        GL20C.glUniformMatrix4fv(location, transpose, value);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void setupOverlayColor(IntSupplier value, int unused) {
        setShaderTexture(1, value.getAsInt());
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void teardownOverlayColor() {
        setShaderTexture(1, 0);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void setupLevelDiffuseLighting(Vector3f vector3f, Vector3f vector3f2, Matrix4f matrix4f) {
        GlStateManager.setupLevelDiffuseLighting(vector3f, vector3f2, matrix4f);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void setupGuiFlatDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        GlStateManager.setupGuiFlatDiffuseLighting(vector3f, vector3f2);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void setupGui3DDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        GlStateManager.setupGui3DDiffuseLighting(vector3f, vector3f2);
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glGenBuffers(Consumer<Integer> consumer) {
        if (!isOnRenderThread()) {
            recordRenderCall(() -> consumer.accept(GL15C.glGenBuffers()));
        } else {
            consumer.accept(GL15C.glGenBuffers());
        }
    }

    /**
     * @author Ocelot
     * @reason Reduce caller overhead
     */
    @Overwrite
    public static void glGenVertexArrays(Consumer<Integer> consumer) {
        if (!isOnRenderThread()) {
            recordRenderCall(() -> consumer.accept(GL30C.glGenVertexArrays()));
        } else {
            consumer.accept(GL30C.glGenVertexArrays());
        }
    }
}
