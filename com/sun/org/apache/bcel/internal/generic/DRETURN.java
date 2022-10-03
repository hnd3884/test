package com.sun.org.apache.bcel.internal.generic;

public class DRETURN extends ReturnInstruction
{
    public DRETURN() {
        super((short)175);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitStackConsumer(this);
        v.visitReturnInstruction(this);
        v.visitDRETURN(this);
    }
}
