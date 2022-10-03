package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantNameAndType extends Constant
{
    private int name_index;
    private int signature_index;
    
    public ConstantNameAndType(final ConstantNameAndType c) {
        this(c.getNameIndex(), c.getSignatureIndex());
    }
    
    ConstantNameAndType(final DataInputStream file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort());
    }
    
    public ConstantNameAndType(final int name_index, final int signature_index) {
        super((byte)12);
        this.name_index = name_index;
        this.signature_index = signature_index;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantNameAndType(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeShort(this.name_index);
        file.writeShort(this.signature_index);
    }
    
    public final int getNameIndex() {
        return this.name_index;
    }
    
    public final String getName(final ConstantPool cp) {
        return cp.constantToString(this.getNameIndex(), (byte)1);
    }
    
    public final int getSignatureIndex() {
        return this.signature_index;
    }
    
    public final String getSignature(final ConstantPool cp) {
        return cp.constantToString(this.getSignatureIndex(), (byte)1);
    }
    
    public final void setNameIndex(final int name_index) {
        this.name_index = name_index;
    }
    
    public final void setSignatureIndex(final int signature_index) {
        this.signature_index = signature_index;
    }
    
    @Override
    public final String toString() {
        return super.toString() + "(name_index = " + this.name_index + ", signature_index = " + this.signature_index + ")";
    }
}
