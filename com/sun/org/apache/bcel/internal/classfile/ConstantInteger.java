package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantInteger extends Constant implements ConstantObject
{
    private int bytes;
    
    public ConstantInteger(final int bytes) {
        super((byte)3);
        this.bytes = bytes;
    }
    
    public ConstantInteger(final ConstantInteger c) {
        this(c.getBytes());
    }
    
    ConstantInteger(final DataInputStream file) throws IOException {
        this(file.readInt());
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantInteger(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeInt(this.bytes);
    }
    
    public final int getBytes() {
        return this.bytes;
    }
    
    public final void setBytes(final int bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public final String toString() {
        return super.toString() + "(bytes = " + this.bytes + ")";
    }
    
    @Override
    public Object getConstantValue(final ConstantPool cp) {
        return new Integer(this.bytes);
    }
}
