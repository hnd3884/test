package com.sun.org.apache.bcel.internal.generic;

public class IXOR extends ArithmeticInstruction
{
    public IXOR() {
        super((short)130);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitIXOR(this);
    }
}
