package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantDouble extends Constant implements ConstantObject
{
    private double bytes;
    
    public ConstantDouble(final double bytes) {
        super((byte)6);
        this.bytes = bytes;
    }
    
    public ConstantDouble(final ConstantDouble c) {
        this(c.getBytes());
    }
    
    ConstantDouble(final DataInputStream file) throws IOException {
        this(file.readDouble());
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantDouble(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeDouble(this.bytes);
    }
    
    public final double getBytes() {
        return this.bytes;
    }
    
    public final void setBytes(final double bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public final String toString() {
        return super.toString() + "(bytes = " + this.bytes + ")";
    }
    
    @Override
    public Object getConstantValue(final ConstantPool cp) {
        return new Double(this.bytes);
    }
}
