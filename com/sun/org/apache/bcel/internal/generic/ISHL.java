package com.sun.org.apache.bcel.internal.generic;

public class ISHL extends ArithmeticInstruction
{
    public ISHL() {
        super((short)120);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitISHL(this);
    }
}
