package com.sun.org.apache.bcel.internal.generic;

public class IF_ACMPEQ extends IfInstruction
{
    IF_ACMPEQ() {
    }
    
    public IF_ACMPEQ(final InstructionHandle target) {
        super((short)165, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IF_ACMPNE(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ACMPEQ(this);
    }
}
