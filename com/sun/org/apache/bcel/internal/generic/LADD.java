package com.sun.org.apache.bcel.internal.generic;

public class LADD extends ArithmeticInstruction
{
    public LADD() {
        super((short)97);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitLADD(this);
    }
}
