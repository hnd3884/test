package com.sun.org.apache.bcel.internal.generic;

public class IADD extends ArithmeticInstruction
{
    public IADD() {
        super((short)96);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitIADD(this);
    }
}
