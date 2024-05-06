package dev.foundry.crucible.mixin.worldgen;

import dev.foundry.crucible.worldgen.CrucibleDensityEnvironment;
import dev.foundry.crucible.worldgen.CrucibleDensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DensityFunctions.Clamp.class)
public class ClampDensityFunctionMixin implements CrucibleDensityFunction {

    @Shadow
    @Final
    private DensityFunction input;

    @Shadow
    @Final
    private double minValue;

    @Shadow
    @Final
    private double maxValue;

    @Override
    public void writeBytecode(ClassNode classNode, MethodNode method, CrucibleDensityEnvironment.Builder environment) {
        CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.input);
        if (this.input.minValue() > this.minValue && this.input.maxValue() < this.maxValue) {
            return;
        }

//        CrucibleDensityFunction.writeDouble(method, this.minValue);
//        method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "max", "(DD)D", false);
//        CrucibleDensityFunction.writeDouble(method, this.maxValue);
//        method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "min", "(DD)D", false);
    }
}
