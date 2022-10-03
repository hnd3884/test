package com.sun.org.apache.bcel.internal.generic;

public class IFLT extends IfInstruction
{
    IFLT() {
    }
    
    public IFLT(final InstructionHandle target) {
        super((short)155, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IFGE(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFLT(this);
    }
}
