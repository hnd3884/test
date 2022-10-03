package com.sun.org.apache.bcel.internal.generic;

public class F2D extends ConversionInstruction
{
    public F2D() {
        super((short)141);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitConversionInstruction(this);
        v.visitF2D(this);
    }
}
