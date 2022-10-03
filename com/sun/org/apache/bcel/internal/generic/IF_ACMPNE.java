package com.sun.org.apache.bcel.internal.generic;

public class IF_ACMPNE extends IfInstruction
{
    IF_ACMPNE() {
    }
    
    public IF_ACMPNE(final InstructionHandle target) {
        super((short)166, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IF_ACMPEQ(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ACMPNE(this);
    }
}
