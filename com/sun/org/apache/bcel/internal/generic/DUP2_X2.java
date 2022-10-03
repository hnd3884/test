package com.sun.org.apache.bcel.internal.generic;

public class DUP2_X2 extends StackInstruction
{
    public DUP2_X2() {
        super((short)94);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackInstruction(this);
        v.visitDUP2_X2(this);
    }
}
