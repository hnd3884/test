package com.sun.org.apache.bcel.internal.generic;

public class I2L extends ConversionInstruction
{
    public I2L() {
        super((short)133);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitConversionInstruction(this);
        v.visitI2L(this);
    }
}
