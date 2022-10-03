package com.sun.org.apache.bcel.internal.generic;

public class DCMPG extends Instruction implements TypedInstruction, StackProducer, StackConsumer
{
    public DCMPG() {
        super((short)152, (short)1);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.DOUBLE;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitDCMPG(this);
    }
}
