package com.sun.org.apache.bcel.internal.generic;

public class INEG extends ArithmeticInstruction
{
    public INEG() {
        super((short)116);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitINEG(this);
    }
}
