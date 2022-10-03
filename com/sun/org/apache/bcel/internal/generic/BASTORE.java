package com.sun.org.apache.bcel.internal.generic;

public class BASTORE extends ArrayInstruction implements StackConsumer
{
    public BASTORE() {
        super((short)84);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitBASTORE(this);
    }
}
