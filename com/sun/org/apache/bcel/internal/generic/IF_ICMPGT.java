package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPGT extends IfInstruction
{
    IF_ICMPGT() {
    }
    
    public IF_ICMPGT(final InstructionHandle target) {
        super((short)163, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IF_ICMPLE(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ICMPGT(this);
    }
}
