package com.sun.org.apache.bcel.internal.generic;

public class DLOAD extends LoadInstruction
{
    DLOAD() {
        super((short)24, (short)38);
    }
    
    public DLOAD(final int n) {
        super((short)24, (short)38, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        super.accept(v);
        v.visitDLOAD(this);
    }
}
