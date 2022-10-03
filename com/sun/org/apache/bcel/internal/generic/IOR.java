package com.sun.org.apache.bcel.internal.generic;

public class IOR extends ArithmeticInstruction
{
    public IOR() {
        super((short)128);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitIOR(this);
    }
}
