package com.sun.org.apache.bcel.internal.generic;

public class FRETURN extends ReturnInstruction
{
    public FRETURN() {
        super((short)174);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitStackConsumer(this);
        v.visitReturnInstruction(this);
        v.visitFRETURN(this);
    }
}
