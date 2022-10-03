package com.sun.org.apache.bcel.internal.generic;

public class DSUB extends ArithmeticInstruction
{
    public DSUB() {
        super((short)103);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitDSUB(this);
    }
}
