package com.sun.org.apache.bcel.internal.generic;

public class IFNONNULL extends IfInstruction
{
    IFNONNULL() {
    }
    
    public IFNONNULL(final InstructionHandle target) {
        super((short)199, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IFNULL(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFNONNULL(this);
    }
}
