package com.sun.org.apache.bcel.internal.generic;

public class DUP_X2 extends StackInstruction
{
    public DUP_X2() {
        super((short)91);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackInstruction(this);
        v.visitDUP_X2(this);
    }
}
