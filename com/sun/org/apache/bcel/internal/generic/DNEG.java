package com.sun.org.apache.bcel.internal.generic;

public class DNEG extends ArithmeticInstruction
{
    public DNEG() {
        super((short)119);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitDNEG(this);
    }
}
