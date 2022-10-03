package com.sun.org.apache.bcel.internal.generic;

public class I2F extends ConversionInstruction
{
    public I2F() {
        super((short)134);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitConversionInstruction(this);
        v.visitI2F(this);
    }
}
