package com.sun.org.apache.bcel.internal.generic;

public abstract class StoreInstruction extends LocalVariableInstruction implements PopInstruction
{
    StoreInstruction(final short canon_tag, final short c_tag) {
        super(canon_tag, c_tag);
    }
    
    protected StoreInstruction(final short opcode, final short c_tag, final int n) {
        super(opcode, c_tag, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitPopInstruction(this);
        v.visitTypedInstruction(this);
        v.visitLocalVariableInstruction(this);
        v.visitStoreInstruction(this);
    }
}
