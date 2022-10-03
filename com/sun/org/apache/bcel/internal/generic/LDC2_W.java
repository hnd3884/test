package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantDouble;
import com.sun.org.apache.bcel.internal.classfile.ConstantLong;

public class LDC2_W extends CPInstruction implements PushInstruction, TypedInstruction
{
    LDC2_W() {
    }
    
    public LDC2_W(final int index) {
        super((short)20, index);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cpg) {
        switch (cpg.getConstantPool().getConstant(this.index).getTag()) {
            case 5: {
                return Type.LONG;
            }
            case 6: {
                return Type.DOUBLE;
            }
            default: {
                throw new RuntimeException("Unknown constant type " + this.opcode);
            }
        }
    }
    
    public Number getValue(final ConstantPoolGen cpg) {
        final Constant c = cpg.getConstantPool().getConstant(this.index);
        switch (c.getTag()) {
            case 5: {
                return new Long(((ConstantLong)c).getBytes());
            }
            case 6: {
                return new Double(((ConstantDouble)c).getBytes());
            }
            default: {
                throw new RuntimeException("Unknown or invalid constant type at " + this.index);
            }
        }
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackProducer(this);
        v.visitPushInstruction(this);
        v.visitTypedInstruction(this);
        v.visitCPInstruction(this);
        v.visitLDC2_W(this);
    }
}
