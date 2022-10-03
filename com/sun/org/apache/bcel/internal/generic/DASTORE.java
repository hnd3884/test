package com.sun.org.apache.bcel.internal.generic;

public class DASTORE extends ArrayInstruction implements StackConsumer
{
    public DASTORE() {
        super((short)82);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitDASTORE(this);
    }
}
