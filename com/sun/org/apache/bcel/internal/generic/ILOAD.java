package com.sun.org.apache.bcel.internal.generic;

public class ILOAD extends LoadInstruction
{
    ILOAD() {
        super((short)21, (short)26);
    }
    
    public ILOAD(final int n) {
        super((short)21, (short)26, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        super.accept(v);
        v.visitILOAD(this);
    }
}
