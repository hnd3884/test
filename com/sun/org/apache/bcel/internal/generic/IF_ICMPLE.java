package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPLE extends IfInstruction
{
    IF_ICMPLE() {
    }
    
    public IF_ICMPLE(final InstructionHandle target) {
        super((short)164, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IF_ICMPGT(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ICMPLE(this);
    }
}
