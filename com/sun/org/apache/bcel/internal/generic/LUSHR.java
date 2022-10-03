package com.sun.org.apache.bcel.internal.generic;

public class LUSHR extends ArithmeticInstruction
{
    public LUSHR() {
        super((short)125);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitLUSHR(this);
    }
}
