package dev.foundry.crucible.worldgen;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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

    public void mapAll(DensityFunction.Visitor visitor) {
        for (int i = 0; i < this.functions.length; i++) {
            this.functions[i] = this.functions[i].mapAll(visitor);
        }
    }

    public static class Builder {

        private final ClassNode classNode;
        private final List<DensityFunction> densityFunctions;
        private final Int2IntMap writtenFunctions;
        private final InsnList variables;
        private int nextLocal;
        private int nextTemp;
        private boolean array;

        private boolean stackConstant;
        private double stackValue;
        private double min;
        private double max;

        public Builder(ClassNode classNode) {
            this.classNode = classNode;
            this.densityFunctions = new ObjectArrayList<>();
            this.writtenFunctions = new Int2IntArrayMap();
            this.variables = new InsnList();
        }

        private int getDensityLocalIndex(DensityFunction function) {
            if (function instanceof CrucibleDensityFunction) {
                throw new IllegalArgumentException("Only vanilla functions should be written to environment");
            }

            int id = -1;
            for (int i = 0; i < this.densityFunctions.size(); i++) {
                DensityFunction densityFunction = this.densityFunctions.get(i);
                if (densityFunction.equals(function)) {
                    if (this.writtenFunctions.containsKey(i)) {
                        return this.writtenFunctions.get(i);
                    } else {
                        id = i;
                        this.densityFunctions.add(function);
                        this.writtenFunctions.put(i, this.nextLocal);
                        break;
                    }
                }
            }

            if (id == -1) {
                id = this.densityFunctions.size();
                this.densityFunctions.add(function);
                this.writtenFunctions.put(id, this.nextLocal);
            }

            this.variables.add(new VarInsnNode(Opcodes.ALOAD, 0));
            this.variables.add(new FieldInsnNode(Opcodes.GETFIELD, this.classNode.name, "environment", "Ldev/foundry/crucible/worldgen/CrucibleDensityEnvironment;"));
            switch (id) {
                case 0 -> this.variables.add(new InsnNode(Opcodes.ICONST_0));
                case 1 -> this.variables.add(new InsnNode(Opcodes.ICONST_1));
                case 2 -> this.variables.add(new InsnNode(Opcodes.ICONST_2));
                case 3 -> this.variables.add(new InsnNode(Opcodes.ICONST_3));
                case 4 -> this.variables.add(new InsnNode(Opcodes.ICONST_4));
                case 5 -> this.variables.add(new InsnNode(Opcodes.ICONST_5));
                default -> this.variables.add(new LdcInsnNode(id));
            }
            this.variables.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "dev/foundry/crucible/worldgen/CrucibleDensityEnvironment", "getFunction", "(I)L" + CrucibleDensityFunctionCompiler.getReferenceName(DensityFunction.class) + ";", false));

            int local = this.writtenFunctions.get(id);
            this.nextLocal++;

            if (this.array) {
                this.variables.add(new VarInsnNode(Opcodes.ALOAD, 1));
                this.variables.add(new InsnNode(Opcodes.ARRAYLENGTH));
                this.variables.add(new VarInsnNode(Opcodes.ALOAD, 2));
                this.variables.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "dev/foundry/crucible/extension/DensityFunctionDuck", "crucible$computeDensity", "(IL" + CrucibleDensityFunctionCompiler.getReferenceName(DensityFunction.ContextProvider.class) + ";)[D", true));
                this.variables.add(new VarInsnNode(Opcodes.ASTORE, local));
                return local;
            }

            this.nextLocal++;
            this.variables.add(new VarInsnNode(Opcodes.ALOAD, 1));
            this.variables.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "dev/foundry/crucible/extension/DensityFunctionDuck", "crucible$computeDensity", "(L" + CrucibleDensityFunctionCompiler.getReferenceName(DensityFunction.FunctionContext.class) + ";)D", true));
            this.variables.add(new VarInsnNode(Opcodes.DSTORE, local));
            return local;
        }

        /**
         * Loads the value for the specifed function onto the stack.
         * @param function
         */
        public void loadDensityFunction(MethodVisitor method, DensityFunction function) {
            int local = this.getDensityLocalIndex(function);
            if (this.array) {
                method.visitVarInsn(Opcodes.ALOAD, local);
                method.visitVarInsn(Opcodes.ILOAD, 3);
                method.visitInsn(Opcodes.DALOAD);
            } else {
                method.visitVarInsn(Opcodes.DLOAD, local);
            }
            this.stackConstant = false;
            this.min = Math.min(this.min, function.minValue());
            this.max = Math.max(this.max, function.maxValue());
        }

        public void loadConstant(MethodVisitor method, double value) {
            CrucibleDensityFunction.writeDouble(method, value);
            this.stackConstant = true;
            this.stackValue = value;
            this.min = Math.min(this.min, value);
            this.max = Math.max(this.max, value);
        }

        public boolean isArray() {
            return this.array;
        }

        public boolean isStackConstant() {
            return this.stackConstant;
        }

        public double getStackValue() {
            return this.stackValue;
        }

        public double getMin() {
            return this.min;
        }

        public double getMax() {
            return this.max;
        }

        public int temp() {
            return this.nextTemp++;
        }

        public int tempDouble() {
            int local = this.temp();
            this.nextTemp++;
            return local;
        }

        public void reset(int nextLocal, boolean array) {
            this.variables.clear();
            this.writtenFunctions.clear();
            this.nextLocal = nextLocal;
            this.nextTemp = 1000;
            this.array = array;
            this.min = Double.POSITIVE_INFINITY;
            this.max = Double.NEGATIVE_INFINITY;
        }

        public InsnList getVariables() {
            return this.variables;
        }

        public CrucibleDensityEnvironment build() {
            return new CrucibleDensityEnvironment(this.densityFunctions.toArray(DensityFunction[]::new));
        }
    }
}
