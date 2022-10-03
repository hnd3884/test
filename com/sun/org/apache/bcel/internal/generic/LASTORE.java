package com.sun.org.apache.bcel.internal.generic;

public class LASTORE extends ArrayInstruction implements StackConsumer
{
    public LASTORE() {
        super((short)80);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitLASTORE(this);
    }
}
