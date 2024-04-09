package dev.foundry.crucible.worldgen;

import net.minecraft.world.level.levelgen.DensityFunction;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.function.Consumer;

/**
 * This allows a density function to be reduced to bytecode.
 */
public interface CrucibleDensityFunction {

    /**
     * Writes the transformation bytecode into the specified method.
     * The stack is expected to have a double value after this method exits.
     *
     * @param method      The method to write into
     * @param environment The compilation environment
     */
    void writeBytecode(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment);

    void writeArrayBytecode(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment);

    /**
     * @return Whether this class is considered the same as all other classes with the same fields
     */
    default boolean canCache() {
        return true;
    }

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

    static void writeEvaluate(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment, DensityFunction function) {
        if (function instanceof CrucibleDensityFunction crucibleDensityFunction) {
            crucibleDensityFunction.writeBytecode(classNode, method, environment);
        } else {
            environment.writeGetFunction(classNode, method, function);
            method.visitVarInsn(Opcodes.ALOAD, 1);
            method.visitMethodInsn(Opcodes.INVOKEINTERFACE, "dev/foundry/crucible/extension/DensityFunctionDuck", "crucible$computeDensity", "(L" + CrucibleDensityFunctionCompiler.getReferenceName(DensityFunction.FunctionContext.class) + ";)D", true);
        }
    }

    static void writeEvaluateArray(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment, DensityFunction function) {
        if (function instanceof CrucibleDensityFunction crucibleDensityFunction) {
            crucibleDensityFunction.writeArrayBytecode(classNode, method, environment);
        } else {
            environment.writeGetFunction(classNode, method, function);
            method.visitVarInsn(Opcodes.ALOAD, 1);
            method.visitVarInsn(Opcodes.ALOAD, 2);
            method.visitMethodInsn(Opcodes.INVOKEINTERFACE, "dev/foundry/crucible/extension/DensityFunctionDuck", "crucible$computeDensity", "([DL" + CrucibleDensityFunctionCompiler.getReferenceName(DensityFunction.ContextProvider.class) + ";)V", true);
        }
    }

    static void writePureTransformerArray(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment, DensityFunction input, Runnable function) {
        CrucibleDensityFunction.writeEvaluateArray(classNode, method, environment, input);

        Label top = new Label();
        Label end = new Label();
        method.visitInsn(Opcodes.ICONST_0);
        method.visitVarInsn(Opcodes.ISTORE, 3);

        method.visitLabel(top);
        method.visitVarInsn(Opcodes.ILOAD, 3);
        method.visitVarInsn(Opcodes.ALOAD, 1);
        method.visitInsn(Opcodes.ARRAYLENGTH);
        method.visitJumpInsn(Opcodes.IF_ICMPGE, end);
        method.visitVarInsn(Opcodes.ALOAD, 1);
        method.visitVarInsn(Opcodes.ILOAD, 3);
        method.visitVarInsn(Opcodes.ALOAD, 0);
        method.visitVarInsn(Opcodes.ALOAD, 1);
        method.visitVarInsn(Opcodes.ILOAD, 3);
        method.visitInsn(Opcodes.DALOAD);
        function.run();
        method.visitInsn(Opcodes.DASTORE);
        method.visitIincInsn(3, 1);
        method.visitJumpInsn(Opcodes.GOTO, top);
        method.visitLabel(end);
        method.visitInsn(Opcodes.RETURN);
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
