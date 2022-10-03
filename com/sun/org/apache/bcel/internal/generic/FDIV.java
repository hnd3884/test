package com.sun.org.apache.bcel.internal.generic;

public class FDIV extends ArithmeticInstruction
{
    public FDIV() {
        super((short)110);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitFDIV(this);
    }
}
