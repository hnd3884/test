package com.sun.org.apache.bcel.internal.generic;

public class LSTORE extends StoreInstruction
{
    LSTORE() {
        super((short)55, (short)63);
    }
    
    public LSTORE(final int n) {
        super((short)55, (short)63, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        super.accept(v);
        v.visitLSTORE(this);
    }
}
