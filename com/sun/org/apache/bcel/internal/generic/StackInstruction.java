package com.sun.org.apache.bcel.internal.generic;

public abstract class StackInstruction extends Instruction
{
    StackInstruction() {
    }
    
    protected StackInstruction(final short opcode) {
        super(opcode, (short)1);
    }
    
    public Type getType(final ConstantPoolGen cp) {
        return Type.UNKNOWN;
    }
}
