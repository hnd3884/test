package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantInteger;
import com.sun.org.apache.bcel.internal.classfile.ConstantFloat;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;
import java.io.DataOutputStream;

public class LDC extends CPInstruction implements PushInstruction, ExceptionThrower, TypedInstruction
{
    LDC() {
    }
    
    public LDC(final int index) {
        super((short)19, index);
        this.setSize();
    }
    
    protected final void setSize() {
        if (this.index <= 255) {
            this.opcode = 18;
            this.length = 2;
        }
        else {
            this.opcode = 19;
            this.length = 3;
        }
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        if (this.length == 2) {
            out.writeByte(this.index);
        }
        else {
            out.writeShort(this.index);
        }
    }
    
    @Override
    public final void setIndex(final int index) {
        super.setIndex(index);
        this.setSize();
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.length = 2;
        this.index = bytes.readUnsignedByte();
    }
    
    public Object getValue(final ConstantPoolGen cpg) {
        Constant c = cpg.getConstantPool().getConstant(this.index);
        switch (c.getTag()) {
            case 8: {
                final int i = ((ConstantString)c).getStringIndex();
                c = cpg.getConstantPool().getConstant(i);
                return ((ConstantUtf8)c).getBytes();
            }
            case 4: {
                return new Float(((ConstantFloat)c).getBytes());
            }
            case 3: {
                return new Integer(((ConstantInteger)c).getBytes());
            }
            default: {
                throw new RuntimeException("Unknown or invalid constant type at " + this.index);
            }
        }
    }
    
    @Override
    public Type getType(final ConstantPoolGen cpg) {
        switch (cpg.getConstantPool().getConstant(this.index).getTag()) {
            case 8: {
                return Type.STRING;
            }
            case 4: {
                return Type.FLOAT;
            }
            case 3: {
                return Type.INT;
            }
            default: {
                throw new RuntimeException("Unknown or invalid constant type at " + this.index);
            }
        }
    }
    
    @Override
    public Class[] getExceptions() {
        return ExceptionConstants.EXCS_STRING_RESOLUTION;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackProducer(this);
        v.visitPushInstruction(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitCPInstruction(this);
        v.visitLDC(this);
    }
}
