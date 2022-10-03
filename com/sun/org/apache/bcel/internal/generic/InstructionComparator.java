package com.sun.org.apache.bcel.internal.generic;

public interface InstructionComparator
{
    public static final InstructionComparator DEFAULT = new InstructionComparator() {
        @Override
        public boolean equals(final Instruction i1, final Instruction i2) {
            if (i1.opcode == i2.opcode) {
                if (i1 instanceof Select) {
                    final InstructionHandle[] t1 = ((Select)i1).getTargets();
                    final InstructionHandle[] t2 = ((Select)i2).getTargets();
                    if (t1.length == t2.length) {
                        for (int j = 0; j < t1.length; ++j) {
                            if (t1[j] != t2[j]) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                else {
                    if (i1 instanceof BranchInstruction) {
                        return false;
                    }
                    if (i1 instanceof ConstantPushInstruction) {
                        return ((ConstantPushInstruction)i1).getValue().equals(((ConstantPushInstruction)i2).getValue());
                    }
                    if (i1 instanceof IndexedInstruction) {
                        return ((IndexedInstruction)i1).getIndex() == ((IndexedInstruction)i2).getIndex();
                    }
                    return !(i1 instanceof NEWARRAY) || ((NEWARRAY)i1).getTypecode() == ((NEWARRAY)i2).getTypecode();
                }
            }
            return false;
        }
    };
    
    boolean equals(final Instruction p0, final Instruction p1);
}
