package com.sun.org.apache.bcel.internal.generic;

public class IFNULL extends IfInstruction
{
    IFNULL() {
    }
    
    public IFNULL(final InstructionHandle target) {
        super((short)198, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IFNONNULL(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFNULL(this);
    }
}
