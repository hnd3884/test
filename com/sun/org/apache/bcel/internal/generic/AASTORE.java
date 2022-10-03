package com.sun.org.apache.bcel.internal.generic;

public class AASTORE extends ArrayInstruction implements StackConsumer
{
    public AASTORE() {
        super((short)83);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitAASTORE(this);
    }
}
