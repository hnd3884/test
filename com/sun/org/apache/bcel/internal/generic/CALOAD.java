package com.sun.org.apache.bcel.internal.generic;

public class CALOAD extends ArrayInstruction implements StackProducer
{
    public CALOAD() {
        super((short)52);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackProducer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitCALOAD(this);
    }
}
