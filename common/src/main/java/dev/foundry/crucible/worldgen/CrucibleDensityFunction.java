package dev.foundry.crucible.worldgen;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * This allows a density function to be reduced to bytecode.
 */
public interface CrucibleDensityFunction {

    /**
     * Writes the transformation bytecode into the specified method.
     * The stack is expected to have a double value after this method exits.
     *
     * @param classNode The class to write into
     * @param method      The method to write into
     * @param environment The compilation environment
     */
    void writeBytecode(ClassNode classNode, MethodNode method, CrucibleDensityEnvironment.Builder environment);

    /**
     * Retrieves the constant value of this node if applicable
     *
     * @return The constant value of this node
     * @throws UnsupportedOperationException If {@link #isConstant()} is <code>false</code>
     */
    default double getConstantValue() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * @return Whether this function is represented by a single constant value
     */
    default boolean isConstant() {
        return false;
    }

    static boolean isConstant(DensityFunction function) {
        return function instanceof CrucibleDensityFunction func && func.isConstant() || function instanceof DensityFunctions.BlendAlpha || function instanceof DensityFunctions.BlendOffset || function instanceof DensityFunctions.Constant;
    }

    static double getConstant(DensityFunction function) {
        if (function instanceof CrucibleDensityFunction func) {
            return func.getConstantValue();
        }
        if (function instanceof DensityFunctions.BlendAlpha) {
            return 1.0;
        }
        if (function instanceof DensityFunctions.BlendOffset) {
            return 0.0;
        }
        return ((DensityFunctions.Constant) function).value();
    }

    static void writeEvaluate(ClassNode classNode, MethodNode method, CrucibleDensityEnvironment.Builder environment, DensityFunction function) {
        function = CrucibleDensityFunctionCompiler.unwrap(function);
        if (isConstant(function)) {
            environment.loadConstant(method, getConstant(function));
            return;
        }

        if (function instanceof CrucibleDensityFunction crucibleDensityFunction) {
            crucibleDensityFunction.writeBytecode(classNode, method, environment);
        } else {
            environment.loadDensityFunction(method, function);
            // clamp
            // yclamp gradient
            // range choice
        }
    }

    static void writeInt(MethodVisitor method, int value) {
        switch (value) {
            case 0 -> method.visitInsn(Opcodes.ICONST_0);
            case 1 -> method.visitInsn(Opcodes.ICONST_1);
            case 2 -> method.visitInsn(Opcodes.ICONST_2);
            case 3 -> method.visitInsn(Opcodes.ICONST_3);
            case 4 -> method.visitInsn(Opcodes.ICONST_4);
            case 5 -> method.visitInsn(Opcodes.ICONST_5);
            default -> method.visitLdcInsn(value);
        }
    }

    static void writeDouble(MethodVisitor method, double value) {
        if (value == 0.0) {
            method.visitInsn(Opcodes.DCONST_0);
        } else if (value == 1.0) {
            method.visitInsn(Opcodes.DCONST_0);
        } else {
            method.visitLdcInsn(value);
        }
    }
}
