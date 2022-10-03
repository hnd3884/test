package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class Synthetic extends Attribute
{
    private byte[] bytes;
    
    public Synthetic(final Synthetic c) {
        this(c.getNameIndex(), c.getLength(), c.getBytes(), c.getConstantPool());
    }
    
    public Synthetic(final int name_index, final int length, final byte[] bytes, final ConstantPool constant_pool) {
        super((byte)7, name_index, length, constant_pool);
        this.bytes = bytes;
    }
    
    Synthetic(final int name_index, final int length, final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, (byte[])null, constant_pool);
        if (length > 0) {
            file.readFully(this.bytes = new byte[length]);
            System.err.println("Synthetic attribute with length > 0");
        }
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitSynthetic(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        super.dump(file);
        if (this.length > 0) {
            file.write(this.bytes, 0, this.length);
        }
    }
    
    public final byte[] getBytes() {
        return this.bytes;
    }
    
    public final void setBytes(final byte[] bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public final String toString() {
        final StringBuffer buf = new StringBuffer("Synthetic");
        if (this.length > 0) {
            buf.append(" " + Utility.toHexString(this.bytes));
        }
        return buf.toString();
    }
    
    @Override
    public Attribute copy(final ConstantPool constant_pool) {
        final Synthetic c = (Synthetic)this.clone();
        if (this.bytes != null) {
            c.bytes = this.bytes.clone();
        }
        c.constant_pool = constant_pool;
        return c;
    }
}
