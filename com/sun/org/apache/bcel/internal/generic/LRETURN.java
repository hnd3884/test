package com.sun.org.apache.bcel.internal.generic;

public class LRETURN extends ReturnInstruction
{
    public LRETURN() {
        super((short)173);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitStackConsumer(this);
        v.visitReturnInstruction(this);
        v.visitLRETURN(this);
    }
}
