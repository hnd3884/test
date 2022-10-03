package com.sun.org.apache.bcel.internal.generic;

public class BALOAD extends ArrayInstruction implements StackProducer
{
    public BALOAD() {
        super((short)51);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackProducer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitBALOAD(this);
    }
}
