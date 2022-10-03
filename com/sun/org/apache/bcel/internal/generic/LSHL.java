package com.sun.org.apache.bcel.internal.generic;

public class LSHL extends ArithmeticInstruction
{
    public LSHL() {
        super((short)121);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitLSHL(this);
    }
}
