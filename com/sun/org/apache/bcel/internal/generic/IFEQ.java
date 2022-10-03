package com.sun.org.apache.bcel.internal.generic;

public class IFEQ extends IfInstruction
{
    IFEQ() {
    }
    
    public IFEQ(final InstructionHandle target) {
        super((short)153, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IFNE(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFEQ(this);
    }
}
