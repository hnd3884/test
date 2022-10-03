package com.sun.org.apache.bcel.internal.generic;

public class DUP_X1 extends StackInstruction
{
    public DUP_X1() {
        super((short)90);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackInstruction(this);
        v.visitDUP_X1(this);
    }
}
