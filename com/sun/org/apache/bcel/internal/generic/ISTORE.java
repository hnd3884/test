package com.sun.org.apache.bcel.internal.generic;

public class ISTORE extends StoreInstruction
{
    ISTORE() {
        super((short)54, (short)59);
    }
    
    public ISTORE(final int n) {
        super((short)54, (short)59, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        super.accept(v);
        v.visitISTORE(this);
    }
}
