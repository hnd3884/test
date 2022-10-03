package com.sun.org.apache.bcel.internal.generic;

public class SWAP extends StackInstruction implements StackConsumer, StackProducer
{
    public SWAP() {
        super((short)95);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitStackProducer(this);
        v.visitStackInstruction(this);
        v.visitSWAP(this);
    }
}
