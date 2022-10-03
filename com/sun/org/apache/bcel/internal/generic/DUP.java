package com.sun.org.apache.bcel.internal.generic;

public class DUP extends StackInstruction implements PushInstruction
{
    public DUP() {
        super((short)89);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackProducer(this);
        v.visitPushInstruction(this);
        v.visitStackInstruction(this);
        v.visitDUP(this);
    }
}
