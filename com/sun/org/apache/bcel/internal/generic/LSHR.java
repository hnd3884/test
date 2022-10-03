package com.sun.org.apache.bcel.internal.generic;

public class LSHR extends ArithmeticInstruction
{
    public LSHR() {
        super((short)123);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitLSHR(this);
    }
}
