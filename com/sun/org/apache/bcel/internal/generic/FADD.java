package com.sun.org.apache.bcel.internal.generic;

public class FADD extends ArithmeticInstruction
{
    public FADD() {
        super((short)98);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitFADD(this);
    }
}
