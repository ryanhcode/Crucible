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

@Mixin(DensityFunctions.MulOrAdd.class)
public class MulOrAddDensityFunctionMixin implements CrucibleDensityFunction {

    @Shadow
    @Final
    private DensityFunctions.MulOrAdd.Type specificType;

    @Shadow
    @Final
    private DensityFunction input;

    @Shadow
    @Final
    private double argument;

    @Override
    public void writeBytecode(ClassNode classNode, MethodNode method, CrucibleDensityEnvironment.Builder environment) {
        CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.input);
        switch (this.specificType) {
            case MUL -> {
                if (this.argument == -1.0) {
                    method.visitInsn(Opcodes.DNEG);
                    break;
                }
                CrucibleDensityFunction.writeDouble(method, this.argument);
                method.visitInsn(Opcodes.DMUL);
            }
            case ADD -> {
                if (this.argument != 0.0) {
                    CrucibleDensityFunction.writeDouble(method, this.argument);
                    method.visitInsn(Opcodes.DADD);
                }
            }
        }
    }

    @Override
    public boolean isConstant() {
        return this.specificType == DensityFunctions.MulOrAdd.Type.MUL && this.argument == 0.0;
    }

    @Override
    public double getConstantValue() throws UnsupportedOperationException {
        return 0.0;
    }
}
