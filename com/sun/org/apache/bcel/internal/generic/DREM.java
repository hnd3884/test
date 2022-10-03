package com.sun.org.apache.bcel.internal.generic;

public class DREM extends ArithmeticInstruction
{
    public DREM() {
        super((short)115);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitDREM(this);
    }
}
