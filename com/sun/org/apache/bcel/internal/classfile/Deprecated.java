package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class Deprecated extends Attribute
{
    private byte[] bytes;
    
    public Deprecated(final Deprecated c) {
        this(c.getNameIndex(), c.getLength(), c.getBytes(), c.getConstantPool());
    }
    
    public Deprecated(final int name_index, final int length, final byte[] bytes, final ConstantPool constant_pool) {
        super((byte)8, name_index, length, constant_pool);
        this.bytes = bytes;
    }
    
    Deprecated(final int name_index, final int length, final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, (byte[])null, constant_pool);
        if (length > 0) {
            file.readFully(this.bytes = new byte[length]);
            System.err.println("Deprecated attribute with length > 0");
        }
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitDeprecated(this);
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
        return Constants.ATTRIBUTE_NAMES[8];
    }
    
    @Override
    public Attribute copy(final ConstantPool constant_pool) {
        final Deprecated c = (Deprecated)this.clone();
        if (this.bytes != null) {
            c.bytes = this.bytes.clone();
        }
        c.constant_pool = constant_pool;
        return c;
    }
}
