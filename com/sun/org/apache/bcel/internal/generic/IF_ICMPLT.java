package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPLT extends IfInstruction
{
    IF_ICMPLT() {
    }
    
    public IF_ICMPLT(final InstructionHandle target) {
        super((short)161, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IF_ICMPGE(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ICMPLT(this);
    }
}
