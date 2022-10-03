package com.sun.org.apache.bcel.internal.generic;

public class DCONST extends Instruction implements ConstantPushInstruction, TypedInstruction
{
    private double value;
    
    DCONST() {
    }
    
    public DCONST(final double f) {
        super((short)14, (short)1);
        if (f == 0.0) {
            this.opcode = 14;
        }
        else {
            if (f != 1.0) {
                throw new ClassGenException("DCONST can be used only for 0.0 and 1.0: " + f);
            }
            this.opcode = 15;
        }
        this.value = f;
    }
    
    @Override
    public Number getValue() {
        return new Double(this.value);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.DOUBLE;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitDCONST(this);
    }
}
