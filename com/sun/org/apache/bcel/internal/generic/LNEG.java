package com.sun.org.apache.bcel.internal.generic;

public class LNEG extends ArithmeticInstruction
{
    public LNEG() {
        super((short)117);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitLNEG(this);
    }
}
