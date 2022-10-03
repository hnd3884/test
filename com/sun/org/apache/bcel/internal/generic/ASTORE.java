package com.sun.org.apache.bcel.internal.generic;

public class ASTORE extends StoreInstruction
{
    ASTORE() {
        super((short)58, (short)75);
    }
    
    public ASTORE(final int n) {
        super((short)58, (short)75, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        super.accept(v);
        v.visitASTORE(this);
    }
}
