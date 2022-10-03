package com.sun.org.apache.bcel.internal.generic;

public class IMPDEP1 extends Instruction
{
    public IMPDEP1() {
        super((short)254, (short)1);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitIMPDEP1(this);
    }
}
