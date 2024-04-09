package dev.foundry.crucible.mixin.worldgen;

import dev.foundry.crucible.worldgen.CrucibleDensityEnvironment;
import dev.foundry.crucible.worldgen.CrucibleDensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DensityFunctions.Constant.class)
public class ConstantDensityFunctionMixin implements CrucibleDensityFunction {

    @Shadow
    @Final
    double value;

    @Override
    public void writeBytecode(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment) {
        CrucibleDensityFunction.writeDouble(method, this.value);
    }

    @Override
    public void writeArrayBytecode(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment) {
        method.visitVarInsn(Opcodes.ALOAD, 1);
        CrucibleDensityFunction.writeDouble(method, this.value);
        method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Arrays", "fill", "([DD)V", false);
    }

    @Override
    public double getConstantValue() throws UnsupportedOperationException {
        return this.value;
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}
