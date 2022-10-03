package com.sun.org.apache.bcel.internal.generic;

public class LAND extends ArithmeticInstruction
{
    public LAND() {
        super((short)127);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitLAND(this);
    }
}
