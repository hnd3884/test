package com.sun.org.apache.bcel.internal.generic;

public class IFGT extends IfInstruction
{
    IFGT() {
    }
    
    public IFGT(final InstructionHandle target) {
        super((short)157, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IFLE(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFGT(this);
    }
}
