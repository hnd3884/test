package com.sun.org.apache.bcel.internal.generic;

public class IFNE extends IfInstruction
{
    IFNE() {
    }
    
    public IFNE(final InstructionHandle target) {
        super((short)154, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IFEQ(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFNE(this);
    }
}
