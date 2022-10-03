package com.sun.org.apache.bcel.internal.generic;

public class SALOAD extends ArrayInstruction implements StackProducer
{
    public SALOAD() {
        super((short)53);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackProducer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitSALOAD(this);
    }
}
