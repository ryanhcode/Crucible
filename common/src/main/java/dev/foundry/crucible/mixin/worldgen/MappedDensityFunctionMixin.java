package dev.foundry.crucible.mixin.worldgen;

import dev.foundry.crucible.worldgen.CrucibleDensityEnvironment;
import dev.foundry.crucible.worldgen.CrucibleDensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DensityFunctions.Mapped.class)
public class MappedDensityFunctionMixin implements CrucibleDensityFunction {

    @Shadow
    @Final
    private DensityFunctions.Mapped.Type type;

    @Shadow
    @Final
    private DensityFunction input;

    @Override
    public void writeBytecode(ClassNode classNode, MethodNode method, CrucibleDensityEnvironment.Builder environment) {
        CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.input);
        switch (this.type) {
            case ABS -> method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "abs", "(D)D", false);
            case SQUARE -> {
                method.visitInsn(Opcodes.DUP2);
                method.visitInsn(Opcodes.DMUL);
            }
            case CUBE -> {
                method.visitInsn(Opcodes.DUP2);
                method.visitInsn(Opcodes.DUP2);
                method.visitInsn(Opcodes.DMUL);
                method.visitInsn(Opcodes.DMUL);
            }
            case HALF_NEGATIVE -> crucible$negative(classNode, method, environment, this.input, 0.5);
            case QUARTER_NEGATIVE -> crucible$negative(classNode, method, environment, this.input, 0.25);
            case SQUEEZE -> {
                int temp = environment.tempDouble();
                Label l2 = new Label();
                Label l3 = new Label();
                method.visitVarInsn(Opcodes.DSTORE, temp);
                method.visitVarInsn(Opcodes.DLOAD, temp);
                method.visitLdcInsn(-1.0);
                method.visitInsn(Opcodes.DCMPG);
                method.visitJumpInsn(Opcodes.IFGE, l2);
                method.visitLdcInsn(-1.0);
                method.visitJumpInsn(Opcodes.GOTO, l3);
                method.visitLabel(l2);
                method.visitVarInsn(Opcodes.DLOAD, temp);
                method.visitInsn(Opcodes.DCONST_1);
                method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "min", "(DD)D", false);
                method.visitLabel(l3);
                method.visitVarInsn(Opcodes.DSTORE, temp);
                method.visitVarInsn(Opcodes.DLOAD, temp);
                method.visitLdcInsn(0.5);
                method.visitInsn(Opcodes.DMUL);
                method.visitVarInsn(Opcodes.DLOAD, temp);
                method.visitVarInsn(Opcodes.DLOAD, temp);
                method.visitInsn(Opcodes.DMUL);
                method.visitVarInsn(Opcodes.DLOAD, temp);
                method.visitInsn(Opcodes.DMUL);
                method.visitLdcInsn(1.0 / 24.0);
                method.visitInsn(Opcodes.DMUL);
                method.visitInsn(Opcodes.DSUB);
            }
        }
    }

    @Unique
    private static void crucible$negative(ClassNode classNode, MethodNode method, CrucibleDensityEnvironment.Builder environment, DensityFunction function, double factor) {
        int temp = environment.tempDouble();
        Label end = new Label();

        CrucibleDensityFunction.writeEvaluate(classNode, method, environment, function);
        method.visitVarInsn(Opcodes.DSTORE, temp);
        method.visitVarInsn(Opcodes.DLOAD, temp);
        method.visitVarInsn(Opcodes.DLOAD, temp);
        method.visitInsn(Opcodes.DCONST_0);
        method.visitInsn(Opcodes.DCMPL);
        method.visitJumpInsn(Opcodes.IFGT, end);
        method.visitLdcInsn(factor);
        method.visitInsn(Opcodes.DMUL);
        method.visitLabel(end);
    }
}
