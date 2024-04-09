package dev.foundry.crucible.worldgen;

import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class CrucibleDensityEnvironment {

    private final DensityFunction[] functions;

    public CrucibleDensityEnvironment(DensityFunction[] functions) {
        this.functions = functions;
    }

    public DensityFunction getFunction(int id) {
        // No checks for speed. This is an internal API
        return this.functions[id];
    }

    public static class Builder {

        private final List<DensityFunction> densityFunctions;

        public Builder() {
            this.densityFunctions = new ArrayList<>();
        }

        public int addDensityFunction(DensityFunction function) {
            if (function instanceof CrucibleDensityFunction) {
                throw new IllegalArgumentException("Only vanilla functions should be written to environment");
            }

            int id = this.densityFunctions.size();
            this.densityFunctions.add(function);
            return id;
        }

        /**
         * Writes the specified vanilla density function onto the stack.
         *
         * @param classNode     The class the function is being written to
         * @param methodVisitor The visitor for building the function
         * @param function      The density function to add
         */
        public void writeGetFunction(ClassNode classNode, MethodVisitor methodVisitor, DensityFunction function) {
            int index = -1;
            for (int i = 0; i < this.densityFunctions.size(); i++) {
                DensityFunction cached = this.densityFunctions.get(i);
                if (cached.equals(function)) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                index = this.addDensityFunction(function);
            }

            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classNode.name, "environment", "Ldev/foundry/crucible/worldgen/CrucibleDensityEnvironment;");
            CrucibleDensityFunction.writeInt(methodVisitor, index);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEDYNAMIC, "dev/foundry/crucible/worldgen/CrucibleDensityEnvironment", "getFunction", "(I)L" + CrucibleDensityFunctionCompiler.getReferenceName(DensityFunction.class) + ";", false);
        }

        public CrucibleDensityEnvironment build() {
            return new CrucibleDensityEnvironment(this.densityFunctions.toArray(DensityFunction[]::new));
        }
    }
}
