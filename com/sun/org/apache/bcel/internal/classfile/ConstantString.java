package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantString extends Constant implements ConstantObject
{
    private int string_index;
    
    public ConstantString(final ConstantString c) {
        this(c.getStringIndex());
    }
    
    ConstantString(final DataInputStream file) throws IOException {
        this(file.readUnsignedShort());
    }
    
    public ConstantString(final int string_index) {
        super((byte)8);
        this.string_index = string_index;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantString(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeShort(this.string_index);
    }
    
    public final int getStringIndex() {
        return this.string_index;
    }
    
    public final void setStringIndex(final int string_index) {
        this.string_index = string_index;
    }
    
    @Override
    public final String toString() {
        return super.toString() + "(string_index = " + this.string_index + ")";
    }
    
    @Override
    public Object getConstantValue(final ConstantPool cp) {
        final Constant c = cp.getConstant(this.string_index, (byte)1);
        return ((ConstantUtf8)c).getBytes();
    }
    
    public String getBytes(final ConstantPool cp) {
        return (String)this.getConstantValue(cp);
    }
}
