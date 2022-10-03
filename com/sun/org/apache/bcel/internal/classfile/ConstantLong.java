package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantLong extends Constant implements ConstantObject
{
    private long bytes;
    
    public ConstantLong(final long bytes) {
        super((byte)5);
        this.bytes = bytes;
    }
    
    public ConstantLong(final ConstantLong c) {
        this(c.getBytes());
    }
    
    ConstantLong(final DataInputStream file) throws IOException {
        this(file.readLong());
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantLong(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeLong(this.bytes);
    }
    
    public final long getBytes() {
        return this.bytes;
    }
    
    public final void setBytes(final long bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public final String toString() {
        return super.toString() + "(bytes = " + this.bytes + ")";
    }
    
    @Override
    public Object getConstantValue(final ConstantPool cp) {
        return new Long(this.bytes);
    }
}
