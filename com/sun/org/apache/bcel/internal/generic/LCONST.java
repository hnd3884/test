package com.sun.org.apache.bcel.internal.generic;

public class LCONST extends Instruction implements ConstantPushInstruction, TypedInstruction
{
    private long value;
    
    LCONST() {
    }
    
    public LCONST(final long l) {
        super((short)9, (short)1);
        if (l == 0L) {
            this.opcode = 9;
        }
        else {
            if (l != 1L) {
                throw new ClassGenException("LCONST can be used only for 0 and 1: " + l);
            }
            this.opcode = 10;
        }
        this.value = l;
    }
    
    @Override
    public Number getValue() {
        return new Long(this.value);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.LONG;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitLCONST(this);
    }
}
