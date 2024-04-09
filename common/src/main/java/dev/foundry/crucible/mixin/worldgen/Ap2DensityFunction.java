package dev.foundry.crucible.mixin.worldgen;

import dev.foundry.crucible.worldgen.CrucibleDensityEnvironment;
import dev.foundry.crucible.worldgen.CrucibleDensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DensityFunctions.Ap2.class)
public class Ap2DensityFunction implements CrucibleDensityFunction {

    @Shadow
    @Final
    private DensityFunction argument1;

    @Shadow
    @Final
    private DensityFunctions.TwoArgumentSimpleFunction.Type type;

    @Shadow
    @Final
    private DensityFunction argument2;

    @Override
    public void writeBytecode(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment) {
//        double d = this.argument1.compute(functionContext);
//        double var10000;
//        switch (this.type) {
//            case ADD -> var10000 = d + this.argument2.compute(functionContext);
//            case MAX ->
//                    var10000 = d > this.argument2.maxValue() ? d : Math.max(d, this.argument2.compute(functionContext));
//            case MIN ->
//                    var10000 = d < this.argument2.minValue() ? d : Math.min(d, this.argument2.compute(functionContext));
//            case MUL -> var10000 = d == 0.0 ? 0.0 : d * this.argument2.compute(functionContext);
//            default -> throw new IncompatibleClassChangeError();
//        }

//        return var10000;

        CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument1);
        switch (this.type) {
            case ADD -> {
                CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument2);
                method.visitInsn(Opcodes.DADD);
                method.visitInsn(Opcodes.DRETURN);
            }
            case MUL -> {
                CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument2);
                method.visitInsn(Opcodes.DMUL);
                method.visitInsn(Opcodes.DRETURN);
            }
            case MIN -> {
                // Since the argument is constant, we can remove the check
                if (this.argument1 instanceof CrucibleDensityFunction func && func.isConstant()) {
                    if (!(func.getConstantValue() > this.argument2.maxValue())) {
                        CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.argument2);
                        method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "max", "(DD)D", false);
                    }
                } else {
                    Label l10 = new Label();
                    Label l7 = new Label();
                    method.visitVarInsn(Opcodes.DSTORE, 2);
                    method.visitVarInsn(Opcodes.DLOAD, 2);
                    CrucibleDensityFunction.writeDouble(method, this.argument2.minValue());
                    method.visitInsn(Opcodes.DCMPG);
                    method.visitJumpInsn(Opcodes.IFGE, l10);
                }
            }
            case MAX -> {
            }
        }
    }

    @Override
    public void writeArrayBytecode(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment) {

    }
}
