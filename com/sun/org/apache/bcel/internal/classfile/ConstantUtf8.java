package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantUtf8 extends Constant
{
    private String bytes;
    
    public ConstantUtf8(final ConstantUtf8 c) {
        this(c.getBytes());
    }
    
    ConstantUtf8(final DataInputStream file) throws IOException {
        super((byte)1);
        this.bytes = file.readUTF();
    }
    
    public ConstantUtf8(final String bytes) {
        super((byte)1);
        if (bytes == null) {
            throw new IllegalArgumentException("bytes must not be null!");
        }
        this.bytes = bytes;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantUtf8(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeUTF(this.bytes);
    }
    
    public final String getBytes() {
        return this.bytes;
    }
    
    public final void setBytes(final String bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public final String toString() {
        return super.toString() + "(\"" + Utility.replace(this.bytes, "\n", "\\n") + "\")";
    }
}
