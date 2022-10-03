package com.sun.org.apache.bcel.internal.generic;

public class IF_ICMPEQ extends IfInstruction
{
    IF_ICMPEQ() {
    }
    
    public IF_ICMPEQ(final InstructionHandle target) {
        super((short)159, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IF_ICMPNE(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ICMPEQ(this);
    }
}
