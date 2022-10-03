package com.sun.org.apache.bcel.internal.generic;

public class FCMPL extends Instruction implements TypedInstruction, StackProducer, StackConsumer
{
    public FCMPL() {
        super((short)149, (short)1);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.FLOAT;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitFCMPL(this);
    }
}
