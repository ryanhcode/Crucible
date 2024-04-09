package dev.foundry.crucible.mixin.worldgen;

import dev.foundry.crucible.worldgen.CrucibleDensityEnvironment;
import dev.foundry.crucible.worldgen.CrucibleDensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
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
    public void writeBytecode(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment) {
        CrucibleDensityFunction.writeEvaluate(classNode, method, environment, this.input);
        CrucibleDensityFunction.writeDouble(method, this.argument);
        switch (this.specificType) {
            case MUL -> method.visitInsn(Opcodes.DMUL);
            case ADD -> method.visitInsn(Opcodes.DADD);
        }
    }

    @Override
    public void writeArrayBytecode(ClassNode classNode, MethodVisitor method, CrucibleDensityEnvironment.Builder environment) {
        CrucibleDensityFunction.writePureTransformerArray(classNode, method, environment, this.input, () -> {
            CrucibleDensityFunction.writeDouble(method, this.argument);
            switch (this.specificType) {
                case MUL -> method.visitInsn(Opcodes.DMUL);
                case ADD -> method.visitInsn(Opcodes.DADD);
            }
        });
    }
}
