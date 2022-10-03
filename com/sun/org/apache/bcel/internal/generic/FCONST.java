package com.sun.org.apache.bcel.internal.generic;

public class FCONST extends Instruction implements ConstantPushInstruction, TypedInstruction
{
    private float value;
    
    FCONST() {
    }
    
    public FCONST(final float f) {
        super((short)11, (short)1);
        if (f == 0.0) {
            this.opcode = 11;
        }
        else if (f == 1.0) {
            this.opcode = 12;
        }
        else {
            if (f != 2.0) {
                throw new ClassGenException("FCONST can be used only for 0.0, 1.0 and 2.0: " + f);
            }
            this.opcode = 13;
        }
        this.value = f;
    }
    
    @Override
    public Number getValue() {
        return new Float(this.value);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.FLOAT;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitFCONST(this);
    }
}
