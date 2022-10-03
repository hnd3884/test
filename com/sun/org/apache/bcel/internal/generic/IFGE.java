package com.sun.org.apache.bcel.internal.generic;

public class IFGE extends IfInstruction
{
    IFGE() {
    }
    
    public IFGE(final InstructionHandle target) {
        super((short)156, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IFLT(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFGE(this);
    }
}
