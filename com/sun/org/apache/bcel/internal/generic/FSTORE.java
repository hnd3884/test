package com.sun.org.apache.bcel.internal.generic;

public class FSTORE extends StoreInstruction
{
    FSTORE() {
        super((short)56, (short)67);
    }
    
    public FSTORE(final int n) {
        super((short)56, (short)67, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        super.accept(v);
        v.visitFSTORE(this);
    }
}
