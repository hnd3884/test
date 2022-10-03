package com.sun.org.apache.bcel.internal.generic;

public abstract class LoadInstruction extends LocalVariableInstruction implements PushInstruction
{
    LoadInstruction(final short canon_tag, final short c_tag) {
        super(canon_tag, c_tag);
    }
    
    protected LoadInstruction(final short opcode, final short c_tag, final int n) {
        super(opcode, c_tag, n);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackProducer(this);
        v.visitPushInstruction(this);
        v.visitTypedInstruction(this);
        v.visitLocalVariableInstruction(this);
        v.visitLoadInstruction(this);
    }
}
