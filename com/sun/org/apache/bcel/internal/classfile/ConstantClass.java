package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantClass extends Constant implements ConstantObject
{
    private int name_index;
    
    public ConstantClass(final ConstantClass c) {
        this(c.getNameIndex());
    }
    
    ConstantClass(final DataInputStream file) throws IOException {
        this(file.readUnsignedShort());
    }
    
    public ConstantClass(final int name_index) {
        super((byte)7);
        this.name_index = name_index;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantClass(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeShort(this.name_index);
    }
    
    public final int getNameIndex() {
        return this.name_index;
    }
    
    public final void setNameIndex(final int name_index) {
        this.name_index = name_index;
    }
    
    @Override
    public Object getConstantValue(final ConstantPool cp) {
        final Constant c = cp.getConstant(this.name_index, (byte)1);
        return ((ConstantUtf8)c).getBytes();
    }
    
    public String getBytes(final ConstantPool cp) {
        return (String)this.getConstantValue(cp);
    }
    
    @Override
    public final String toString() {
        return super.toString() + "(name_index = " + this.name_index + ")";
    }
}
