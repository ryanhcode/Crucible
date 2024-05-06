package dev.foundry.crucible.worldgen;

import com.mojang.serialization.Codec;
import dev.foundry.crucible.Crucible;
import dev.foundry.crucible.CruciblePlatform;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@ApiStatus.Internal
public class CrucibleDensityFunctionCompiler extends ClassLoader {

    private static final Map<Class<?>, String> MAPPED_CLASSES;
    private static final Pattern DASH = Pattern.compile("-");

    private final RegistryAccess registryAccess;
    private final boolean writeClasses;

    static {
        Set<Class<?>> classes = Set.of(
                NormalNoise.class,
                DensityFunctions.Noise.class,
                DensityFunction.NoiseHolder.class,
                DensityFunction.class,
                DensityFunction.Visitor.class,
                DensityFunction.SimpleFunction.class,
                DensityFunction.FunctionContext.class,
                DensityFunction.ContextProvider.class,
                KeyDispatchDataCodec.class);
        MAPPED_CLASSES = new Object2ObjectArrayMap<>();
        for (Class<?> clazz : classes) {
            MAPPED_CLASSES.put(clazz, clazz.getTypeName().replaceAll("\\.", "/"));
        }
    }

    public CrucibleDensityFunctionCompiler(ClassLoader parent, RegistryAccess registryAccess) {
        this(parent, registryAccess, false);
    }

    public CrucibleDensityFunctionCompiler(ClassLoader parent, RegistryAccess registryAccess, boolean writeClasses) {
        super(parent);
        this.registryAccess = registryAccess;
        this.writeClasses = writeClasses;
    }

    @SuppressWarnings({"JavaReflectionInvocation", "ConstantValue"})
    private DensityFunction generateClass(CrucibleDensityFunction function) throws IOException, ReflectiveOperationException {
        String name = "Density_" + DASH.matcher(UUID.randomUUID().toString()).replaceAll("");
        try {
            DensityFunction vanillaFunction = (DensityFunction) function;
            boolean holder = (Object) function instanceof DensityFunctions.HolderHolder;
            ClassNode classNode = new ClassNode(Opcodes.ASM5);
            classNode.version = Opcodes.V1_8;
            classNode.superName = "java/lang/Object";
            classNode.name = name;
            classNode.access = Opcodes.ACC_PUBLIC;
            classNode.interfaces.add(getReferenceName(DensityFunction.SimpleFunction.class));

            classNode.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "environment", "Ldev/foundry/crucible/worldgen/CrucibleDensityEnvironment;", null, null);
            classNode.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "codec", "L" + getReferenceName(KeyDispatchDataCodec.class) + ";", null, null);
            if (holder) {
                classNode.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "function", "L" + getReferenceName(DensityFunctions.HolderHolder.class) + ";", null, null);
            }

            MethodNode init = new MethodNode();
            init.access = Opcodes.ACC_PUBLIC;
            init.name = "<init>";
            if (holder) {
                init.desc = "(L" + getReferenceName(KeyDispatchDataCodec.class) + ";Ldev/foundry/crucible/worldgen/CrucibleDensityEnvironment;L" + getReferenceName(DensityFunctions.HolderHolder.class) + ";)V";
            } else {
                init.desc = "(L" + getReferenceName(KeyDispatchDataCodec.class) + ";Ldev/foundry/crucible/worldgen/CrucibleDensityEnvironment;)V";
            }
            init.visitVarInsn(Opcodes.ALOAD, 0);
            init.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            init.visitVarInsn(Opcodes.ALOAD, 0);
            init.visitVarInsn(Opcodes.ALOAD, 1);
            init.visitFieldInsn(Opcodes.PUTFIELD, classNode.name, "codec", "L" + getReferenceName(KeyDispatchDataCodec.class) + ";");
            init.visitVarInsn(Opcodes.ALOAD, 0);
            init.visitVarInsn(Opcodes.ALOAD, 2);
            init.visitFieldInsn(Opcodes.PUTFIELD, classNode.name, "environment", "Ldev/foundry/crucible/worldgen/CrucibleDensityEnvironment;");
            if (holder) {
                init.visitVarInsn(Opcodes.ALOAD, 0);
                init.visitVarInsn(Opcodes.ALOAD, 3);
                init.visitFieldInsn(Opcodes.PUTFIELD, classNode.name, "function", "L" + getReferenceName(DensityFunctions.HolderHolder.class) + ";");
            }
            init.visitInsn(Opcodes.RETURN);
            classNode.methods.add(init);

            CrucibleDensityEnvironment.Builder builder = new CrucibleDensityEnvironment.Builder(classNode);
            MethodNode compute = new MethodNode();
            compute.access = Opcodes.ACC_PUBLIC;
            compute.desc = "(L" + getReferenceName(DensityFunction.FunctionContext.class) + ";)D";
            compute.name = CruciblePlatform.mapMethod(DensityFunction.class, "m_207386_", "compute", compute.desc);
            builder.reset(2, false);
            function.writeBytecode(classNode, compute, builder);
            compute.visitInsn(Opcodes.DRETURN);
            compute.instructions.insertBefore(compute.instructions.getFirst(), builder.getVariables());
            classNode.methods.add(compute);

            MethodNode fillArray = new MethodNode();
            fillArray.access = Opcodes.ACC_PUBLIC;
            fillArray.desc = "([DL" + getReferenceName(DensityFunction.ContextProvider.class) + ";)V";
            fillArray.name = CruciblePlatform.mapMethod(DensityFunction.class, "m_207362_", "fillArray", fillArray.desc);
            builder.reset(4, true);
            {
                Label top = new Label();
                Label end = new Label();
                fillArray.visitInsn(Opcodes.ICONST_0);
                fillArray.visitVarInsn(Opcodes.ISTORE, 3);

                fillArray.visitLabel(top);
                fillArray.visitVarInsn(Opcodes.ILOAD, 3);
                fillArray.visitVarInsn(Opcodes.ALOAD, 1);
                fillArray.visitInsn(Opcodes.ARRAYLENGTH);
                fillArray.visitJumpInsn(Opcodes.IF_ICMPGE, end);
                fillArray.visitVarInsn(Opcodes.ALOAD, 1);
                fillArray.visitVarInsn(Opcodes.ILOAD, 3);
                function.writeBytecode(classNode, fillArray, builder);
                fillArray.visitInsn(Opcodes.DASTORE);
                fillArray.visitIincInsn(3, 1);
                fillArray.visitJumpInsn(Opcodes.GOTO, top);
                fillArray.visitLabel(end);
            }
            fillArray.visitInsn(Opcodes.RETURN);
            fillArray.instructions.insertBefore(fillArray.instructions.getFirst(), builder.getVariables());
            classNode.methods.add(fillArray);

            MethodNode mapAll = new MethodNode();
            mapAll.access = Opcodes.ACC_PUBLIC;
            mapAll.desc = "(L" + getReferenceName(DensityFunction.Visitor.class) + ";)L" + getReferenceName(DensityFunction.class) + ";";
            mapAll.name = CruciblePlatform.mapMethod(DensityFunction.class, "m_207456_", "mapAll", mapAll.desc);
            mapAll.visitVarInsn(Opcodes.ALOAD, 0);
            mapAll.visitFieldInsn(Opcodes.GETFIELD, classNode.name, "environment", "Ldev/foundry/crucible/worldgen/CrucibleDensityEnvironment;");
            mapAll.visitVarInsn(Opcodes.ALOAD, 1);
            mapAll.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "dev/foundry/crucible/worldgen/CrucibleDensityEnvironment", "mapAll", "(L" + getReferenceName(DensityFunction.Visitor.class) + ";)V", false);
            mapAll.visitVarInsn(Opcodes.ALOAD, 0);
            mapAll.visitInsn(Opcodes.ARETURN);
            classNode.methods.add(mapAll);

            MethodNode minValue = new MethodNode();
            minValue.access = Opcodes.ACC_PUBLIC;
            minValue.desc = "()D";
            minValue.name = CruciblePlatform.mapMethod(DensityFunction.class, "m_207402_", "minValue", minValue.desc);
            CrucibleDensityFunction.writeDouble(minValue, vanillaFunction.minValue());
            minValue.visitInsn(Opcodes.DRETURN);
            classNode.methods.add(minValue);

            MethodNode maxValue = new MethodNode();
            maxValue.access = Opcodes.ACC_PUBLIC;
            maxValue.desc = "()D";
            maxValue.name = CruciblePlatform.mapMethod(DensityFunction.class, "m_207401_", "maxValue", maxValue.desc);
            CrucibleDensityFunction.writeDouble(maxValue, vanillaFunction.maxValue());
            maxValue.visitInsn(Opcodes.DRETURN);
            classNode.methods.add(maxValue);

            MethodNode codecMethod = new MethodNode();
            codecMethod.access = Opcodes.ACC_PUBLIC;
            codecMethod.desc = "()L" + getReferenceName(KeyDispatchDataCodec.class) + ";";
            codecMethod.name = CruciblePlatform.mapMethod(DensityFunction.class, "m_214023_", "codec", codecMethod.desc);
            codecMethod.visitVarInsn(Opcodes.ALOAD, 0);
            codecMethod.visitFieldInsn(Opcodes.GETFIELD, classNode.name, "codec", "L" + getReferenceName(KeyDispatchDataCodec.class) + ";");
            codecMethod.visitInsn(Opcodes.ARETURN);
            classNode.methods.add(codecMethod);

            Registry<DensityFunction> registry = this.registryAccess.registryOrThrow(Registries.DENSITY_FUNCTION);
            String key = registry.getResourceKey(vanillaFunction).map(resourceKey -> resourceKey.location().toString()).orElseGet(() -> {
//                DataResult<JsonElement> result = DensityFunction.HOLDER_HELPER_CODEC.encodeStart(JsonOps.COMPRESSED, vanillaFunction);
//                if (result.error().isPresent()) {
//                    throw new JsonSyntaxException(result.error().get().message());
//                }
//                return result.result().orElseThrow().toString();
                return Integer.toString(vanillaFunction.hashCode());
            });

            MethodNode equals = new MethodNode();
            Label equalsFail = new Label();
            Label equalsReturn = new Label();
            equals.access = Opcodes.ACC_PUBLIC;
            equals.name = "equals";
            equals.desc = "(Ljava/lang/Object;)Z";

            equals.visitVarInsn(Opcodes.ALOAD, 1);
            equals.visitTypeInsn(Opcodes.INSTANCEOF, getReferenceName(DensityFunction.class));
            equals.visitJumpInsn(Opcodes.IFEQ, equalsFail); // if !(obj instanceof MolangExpression) goto equalsFail

            equals.visitLdcInsn(key);
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
            CrucibleDensityFunction.writeInt(hashCode, key.hashCode());
            hashCode.visitInsn(Opcodes.IRETURN);
            classNode.methods.add(hashCode);

            MethodNode toString = new MethodNode();
            toString.access = Opcodes.ACC_PUBLIC;
            toString.name = "toString";
            toString.desc = "()Ljava/lang/String;";
            toString.visitLdcInsn(key);
            toString.visitInsn(Opcodes.ARETURN);
            classNode.methods.add(toString);

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            classNode.accept(cw);
            byte[] data = cw.toByteArray();

            if (this.writeClasses) {
                Path path = Paths.get(Crucible.MOD_ID + "-density-debug", classNode.name + ".class");
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                if (!Files.exists(path)) {
                    Files.createFile(path);
                }
                Files.write(path, data);
            }

            KeyDispatchDataCodec<? extends DensityFunction> codec = KeyDispatchDataCodec.of(Codec.unit(vanillaFunction));
            Class<?> clazz = this.defineClass(classNode.name, data, 0, data.length);
            return (DensityFunction) (holder ?
                    clazz.getConstructor(KeyDispatchDataCodec.class, CrucibleDensityEnvironment.class, DensityFunctions.HolderHolder.class).newInstance(codec, builder.build(), function) :
                    clazz.getConstructor(KeyDispatchDataCodec.class, CrucibleDensityEnvironment.class).newInstance(codec, builder.build()));
        } catch (Throwable t) {
            Crucible.LOGGER.error("Error for class: {}", name);
            throw t;
        }
    }

    public DensityFunction compile(DensityFunction function) {
        function = unwrap(function);

        // The function is of some unknown type
        if (!(function instanceof CrucibleDensityFunction crucibleDensityFunction)) {
            return function;
        }

        try {
            return this.generateClass(crucibleDensityFunction);
        } catch (Throwable t) {
            String name = function.toString();
            if (name.length() > 800) {
                name = name.substring(0, 800) + "...";
            }
            Crucible.LOGGER.error("Failed to convert density function '{}' to bytecode", name, t);
            return function;
        }
    }

    public static DensityFunction unwrap(DensityFunction function) {
        while (true) {
            if (function instanceof DensityFunctions.Marker marker) {
                function = marker.wrapped();
                continue;
            }
            if (function instanceof DensityFunctions.HolderHolder holderHolder) {
                function = holderHolder.function().value();
                continue;
            }
            break;
        }
        return function;
    }

    public static String getReferenceName(Class<?> clazz) {
        String name = MAPPED_CLASSES.get(clazz);
        if (name == null) {
            throw new IllegalStateException("Unmapped reference for class: " + clazz.getName());
        }
        return name;
    }
}
