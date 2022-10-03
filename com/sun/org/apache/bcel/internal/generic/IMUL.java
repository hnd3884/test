package com.sun.org.apache.bcel.internal.generic;

public class IMUL extends ArithmeticInstruction
{
    public IMUL() {
        super((short)104);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitIMUL(this);
    }
}
