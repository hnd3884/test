package com.sun.org.apache.bcel.internal.generic;

public class RETURN extends ReturnInstruction
{
    public RETURN() {
        super((short)177);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitStackConsumer(this);
        v.visitReturnInstruction(this);
        v.visitRETURN(this);
    }
}
