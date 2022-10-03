package com.sun.org.apache.bcel.internal.generic;

public class I2C extends ConversionInstruction
{
    public I2C() {
        super((short)146);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitConversionInstruction(this);
        v.visitI2C(this);
    }
}
