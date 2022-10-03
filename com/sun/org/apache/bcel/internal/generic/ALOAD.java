package com.sun.org.apache.bcel.internal.generic;

public class ALOAD extends LoadInstruction
{
    ALOAD() {
        super((short)25, (short)42);
    }
    
    public ALOAD(final int n) {
        super((short)25, (short)42, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        super.accept(v);
        v.visitALOAD(this);
    }
}
