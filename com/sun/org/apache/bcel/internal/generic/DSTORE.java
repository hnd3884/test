package com.sun.org.apache.bcel.internal.generic;

public class DSTORE extends StoreInstruction
{
    DSTORE() {
        super((short)57, (short)71);
    }
    
    public DSTORE(final int n) {
        super((short)57, (short)71, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        super.accept(v);
        v.visitDSTORE(this);
    }
}
