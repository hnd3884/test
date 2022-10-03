package com.sun.org.apache.bcel.internal.generic;

public class DADD extends ArithmeticInstruction
{
    public DADD() {
        super((short)99);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitDADD(this);
    }
}
