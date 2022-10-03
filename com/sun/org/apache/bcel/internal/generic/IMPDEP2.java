package com.sun.org.apache.bcel.internal.generic;

public class IMPDEP2 extends Instruction
{
    public IMPDEP2() {
        super((short)255, (short)1);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitIMPDEP2(this);
    }
}
