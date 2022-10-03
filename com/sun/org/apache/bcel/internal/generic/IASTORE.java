package com.sun.org.apache.bcel.internal.generic;

public class IASTORE extends ArrayInstruction implements StackConsumer
{
    public IASTORE() {
        super((short)79);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitIASTORE(this);
    }
}
