package com.sun.org.apache.bcel.internal.generic;

public class LCMP extends Instruction implements TypedInstruction, StackProducer, StackConsumer
{
    public LCMP() {
        super((short)148, (short)1);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.LONG;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitLCMP(this);
    }
}
