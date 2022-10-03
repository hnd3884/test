package com.sun.org.apache.bcel.internal.generic;

public class I2D extends ConversionInstruction
{
    public I2D() {
        super((short)135);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitConversionInstruction(this);
        v.visitI2D(this);
    }
}
