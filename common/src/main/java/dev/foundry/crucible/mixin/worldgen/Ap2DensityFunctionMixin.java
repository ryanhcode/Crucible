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

@Mixin(DensityFunctions.Ap2.class)
public abstract class Ap2DensityFunctionMixin implements CrucibleDensityFunction {

    @Shadow
    @Final
    private DensityFunction argument1;

    @Shadow
    @Final
    private DensityFunctions.TwoArgumentSimpleFunction.Type type;

    @Shadow
    @Final
    private DensityFunction argument2;

    @Shadow
    public abstract double compute(DensityFunction.FunctionContext arg);

    @Override
    public void writeBytecode(ClassNode classNode, MethodNode method, CrucibleDensityEnvironment.Builder environment) {
        CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument1);
        switch (this.type) {
            case ADD -> {
                CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument2);
                method.visitInsn(Opcodes.DADD);
            }
            case MUL -> {
                if (CrucibleDensityFunction.isConstant(this.argument2)) {
                    if (CrucibleDensityFunction.getConstant(this.argument2) == -1.0) {
                        method.visitInsn(Opcodes.DNEG);
                        break;
                    }
                }
                CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument2);
                method.visitInsn(Opcodes.DMUL);
            }
            case MAX -> {
                // Since the argument is constant, we can remove the check
                if (CrucibleDensityFunction.isConstant(this.argument1)) {
                    if (CrucibleDensityFunction.getConstant(this.argument1) < this.argument2.maxValue()) {
                        CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument2);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "max", "(DD)D", false);
                    }
                } else {
                    Label max = new Label();
                    Label end = new Label();
                    int temp = environment.tempDouble();
                    method.visitVarInsn(Opcodes.DSTORE, temp);
                    method.visitVarInsn(Opcodes.DLOAD, temp);
                    CrucibleDensityFunction.writeDouble(method, this.argument2.maxValue());
                    method.visitInsn(Opcodes.DCMPL);
                    method.visitJumpInsn(Opcodes.IFLE, max);
                    method.visitVarInsn(Opcodes.DLOAD, temp);
                    method.visitJumpInsn(Opcodes.GOTO, end);
                    method.visitLabel(max);
                    method.visitVarInsn(Opcodes.DLOAD, temp);
                    CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument2);
                    method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "max", "(DD)D", false);
                    method.visitLabel(end);
                }
            }
            case MIN -> {
                // Since the argument is constant, we can remove the check
                if (CrucibleDensityFunction.isConstant(this.argument1)) {
                    if (CrucibleDensityFunction.getConstant(this.argument1) > this.argument2.minValue()) {
                        CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument2);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "min", "(DD)D", false);
                    }
                } else {
                    Label min = new Label();
                    Label end = new Label();
                    int temp = environment.tempDouble();
                    method.visitVarInsn(Opcodes.DSTORE, temp);
                    method.visitVarInsn(Opcodes.DLOAD, temp);
                    CrucibleDensityFunction.writeDouble(method, this.argument2.minValue());
                    method.visitInsn(Opcodes.DCMPG);
                    method.visitJumpInsn(Opcodes.IFLE, min);
                    method.visitVarInsn(Opcodes.DLOAD, temp);
                    method.visitJumpInsn(Opcodes.GOTO, end);
                    method.visitLabel(min);
                    method.visitVarInsn(Opcodes.DLOAD, temp);
                    CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument2);
                    method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "min", "(DD)D", false);
                    method.visitLabel(end);
                }
            }
        }
    }

    @Override
    public boolean isConstant() {
        return CrucibleDensityFunction.isConstant(this.argument1) && CrucibleDensityFunction.isConstant(this.argument2);
    }

    @Override
    public double getConstantValue() throws UnsupportedOperationException {
        return this.compute(null);
    }
}
