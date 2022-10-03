package com.sun.org.apache.bcel.internal.generic;

public class FASTORE extends ArrayInstruction implements StackConsumer
{
    public FASTORE() {
        super((short)81);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitFASTORE(this);
    }
}
