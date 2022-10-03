package com.sun.org.apache.bcel.internal.generic;

public class ICONST extends Instruction implements ConstantPushInstruction, TypedInstruction
{
    private int value;
    
    ICONST() {
    }
    
    public ICONST(final int i) {
        super((short)3, (short)1);
        if (i >= -1 && i <= 5) {
            this.opcode = (short)(3 + i);
            this.value = i;
            return;
        }
        throw new ClassGenException("ICONST can be used only for value between -1 and 5: " + i);
    }
    
    @Override
    public Number getValue() {
        return new Integer(this.value);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.INT;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitICONST(this);
    }
}
