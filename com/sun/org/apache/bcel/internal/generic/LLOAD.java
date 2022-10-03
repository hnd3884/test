package com.sun.org.apache.bcel.internal.generic;

public class LLOAD extends LoadInstruction
{
    LLOAD() {
        super((short)22, (short)30);
    }
    
    public LLOAD(final int n) {
        super((short)22, (short)30, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        super.accept(v);
        v.visitLLOAD(this);
    }
}
