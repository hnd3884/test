package com.sun.org.apache.bcel.internal.generic;

public class LMUL extends ArithmeticInstruction
{
    public LMUL() {
        super((short)105);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitLMUL(this);
    }
}
