package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantFloat extends Constant implements ConstantObject
{
    private float bytes;
    
    public ConstantFloat(final float bytes) {
        super((byte)4);
        this.bytes = bytes;
    }
    
    public ConstantFloat(final ConstantFloat c) {
        this(c.getBytes());
    }
    
    ConstantFloat(final DataInputStream file) throws IOException {
        this(file.readFloat());
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantFloat(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeFloat(this.bytes);
    }
    
    public final float getBytes() {
        return this.bytes;
    }
    
    public final void setBytes(final float bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public final String toString() {
        return super.toString() + "(bytes = " + this.bytes + ")";
    }
    
    @Override
    public Object getConstantValue(final ConstantPool cp) {
        return new Float(this.bytes);
    }
}
