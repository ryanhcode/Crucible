package dev.foundry.crucible.worldgen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.foundry.crucible.Crucible;
import dev.foundry.crucible.CruciblePlatform;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class CrucibleDensityFunctionCompiler extends ClassLoader {

    private static final Map<Class<?>, String> MAPPED_CLASSES = mapClass(DensityFunction.class, DensityFunction.FunctionContext.class);
    private static final Pattern DASH = Pattern.compile("-");

    private final Map<DensityFunction, DensityFunction> densityFunctionCache;
    private final Lock classLock;
    private final boolean writeClasses;

    public CrucibleDensityFunctionCompiler(ClassLoader parent) {
        this(parent, false);
    }

    public CrucibleDensityFunctionCompiler(ClassLoader parent, boolean writeClasses) {
        super(parent);
        this.densityFunctionCache = new Object2ObjectArrayMap<>();
        this.classLock = new ReentrantLock();
        this.writeClasses = writeClasses;
    }

    private static Map<Class<?>, String> mapClass(Class<?>... classes) {
        Map<Class<?>, String> names = new Object2ObjectArrayMap<>();
        for (Class<?> clazz : classes) {
            names.put(clazz, clazz.getName().replaceAll("\\.", "/"));
        }
        return names;
    }

    private DensityFunction generateClass(CrucibleDensityFunction function) {
        try {
            CrucibleDensityEnvironment.Builder builder = new CrucibleDensityEnvironment.Builder();
            ClassNode classNode = new ClassNode(Opcodes.ASM5);
            classNode.version = Opcodes.V1_8;
            classNode.superName = "java/lang/Object";
            classNode.name = "Density_" + DASH.matcher(UUID.randomUUID().toString()).replaceAll("");
            classNode.access = Opcodes.ACC_PUBLIC;
            classNode.interfaces.add(getReferenceName(DensityFunction.SimpleFunction.class));

            MethodNode init = new MethodNode();
            init.access = Opcodes.ACC_PUBLIC;
            init.name = "<init>";
            init.desc = "()V";
            init.visitVarInsn(Opcodes.ALOAD, 0);
            init.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            init.visitInsn(Opcodes.RETURN);
            classNode.methods.add(init);

            MethodNode method = new MethodNode();
            method.access = Opcodes.ACC_PUBLIC;
            method.desc = "(L" + getReferenceName(DensityFunction.FunctionContext.class) + ";)D";
            method.name = CruciblePlatform.mapMethod(getReferenceName(DensityFunction.class), "compute", method.desc);
            function.writeBytecode(classNode, method, builder);
            classNode.methods.add(method);

            DataResult<JsonElement> result = DensityFunction.HOLDER_HELPER_CODEC.encodeStart(JsonOps.COMPRESSED, (DensityFunction) function);
            if (result.error().isPresent()) {
                throw new JsonSyntaxException(result.error().get().message());
            }
            String compiledSource = result.result().orElseThrow().toString();

            MethodNode equals = new MethodNode();
            Label equalsFail = new Label();
            Label equalsReturn = new Label();
            equals.access = Opcodes.ACC_PUBLIC;
            equals.name = "equals";
            equals.desc = "(Ljava/lang/Object;)Z";

            equals.visitVarInsn(Opcodes.ALOAD, 1);
            equals.visitTypeInsn(Opcodes.INSTANCEOF, getReferenceName(DensityFunction.class));
            equals.visitJumpInsn(Opcodes.IFEQ, equalsFail); // if !(obj instanceof MolangExpression) goto equalsFail

            equals.visitLdcInsn(compiledSource);
            equals.visitVarInsn(Opcodes.ALOAD, 1);
            equals.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
            equals.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            equals.visitJumpInsn(Opcodes.IFEQ, equalsFail); // if !source.equals(obj.toString()) goto equalsFail

            CrucibleDensityFunction.writeInt(equals, 1);
            equals.visitJumpInsn(Opcodes.GOTO, equalsReturn);

            equals.visitLabel(equalsFail);
            CrucibleDensityFunction.writeInt(equals, 0);

            equals.visitLabel(equalsReturn);
            equals.visitInsn(Opcodes.IRETURN);

            classNode.methods.add(equals);

            MethodNode hashCode = new MethodNode();
            hashCode.access = Opcodes.ACC_PUBLIC;
            hashCode.name = "hashCode";
            hashCode.desc = "()I";
            CrucibleDensityFunction.writeInt(hashCode, compiledSource.hashCode());
            hashCode.visitInsn(Opcodes.IRETURN);
            classNode.methods.add(hashCode);

            MethodNode toString = new MethodNode();
            toString.access = Opcodes.ACC_PUBLIC;
            toString.name = "toString";
            toString.desc = "()Ljava/lang/String;";
            toString.visitLdcInsn(compiledSource);
            toString.visitInsn(Opcodes.ARETURN);
            classNode.methods.add(toString);

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            classNode.accept(cw);
            byte[] data = cw.toByteArray();

            if (this.writeClasses) {
                Path path = Paths.get(classNode.name + ".class");
                if (!Files.exists(path)) {
                    Files.createFile(path);
                }
                Files.write(path, data);
            }

            return (DensityFunction) this.defineClass(classNode.name, data, 0, data.length).getConstructor().newInstance();
        } catch (Throwable t) {
            Crucible.LOGGER.error("Failed to convert density function '{}' to bytecode", function, t);
            return (DensityFunction) function;
        }
    }

    public DensityFunction compile(DensityFunction function) {
        // The function is of some unknown type
        if (!(function instanceof CrucibleDensityFunction crucibleDensityFunction)) {
            return function;
        }

        if (!crucibleDensityFunction.canCache()) {
            return this.generateClass(crucibleDensityFunction);
        }

        // The function is expected to be encapsulated, so it can be used as a map key
        DensityFunction cached = this.densityFunctionCache.get(function);
        if (cached != null) {
            return cached;
        }

        try {
            this.classLock.lock();

            // Make sure no other thread has defined the class
            cached = this.densityFunctionCache.get(function);
            if (cached != null) {
                return cached;
            }

            // Generate the bytecode
            DensityFunction value = this.generateClass(crucibleDensityFunction);
            this.densityFunctionCache.put(function, value);
            return value;
        } finally {
            this.classLock.unlock();
        }
    }

    public static String getReferenceName(Class<?> clazz) {
        String name = MAPPED_CLASSES.get(clazz);
        if (name == null) {
            throw new IllegalStateException("Unmapped reference for class: " + clazz.getName());
        }
        return name;
    }
}
