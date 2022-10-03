package com.sun.org.apache.bcel.internal.generic;

public class DDIV extends ArithmeticInstruction
{
    public DDIV() {
        super((short)111);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitDDIV(this);
    }
}
