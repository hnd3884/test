package com.sun.org.apache.bcel.internal.generic;

public class ACONST_NULL extends Instruction implements PushInstruction, TypedInstruction
{
    public ACONST_NULL() {
        super((short)1, (short)1);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.NULL;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackProducer(this);
        v.visitPushInstruction(this);
        v.visitTypedInstruction(this);
        v.visitACONST_NULL(this);
    }
}
