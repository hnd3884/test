package com.sun.org.apache.bcel.internal.generic;

public class F2L extends ConversionInstruction
{
    public F2L() {
        super((short)140);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitConversionInstruction(this);
        v.visitF2L(this);
    }
}
