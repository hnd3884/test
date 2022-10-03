package com.sun.org.apache.bcel.internal.generic;

public class LXOR extends ArithmeticInstruction
{
    public LXOR() {
        super((short)131);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitLXOR(this);
    }
}
