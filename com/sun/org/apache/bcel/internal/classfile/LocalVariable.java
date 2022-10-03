package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.Serializable;
import com.sun.org.apache.bcel.internal.Constants;

public final class LocalVariable implements Constants, Cloneable, Node, Serializable
{
    private int start_pc;
    private int length;
    private int name_index;
    private int signature_index;
    private int index;
    private ConstantPool constant_pool;
    
    public LocalVariable(final LocalVariable c) {
        this(c.getStartPC(), c.getLength(), c.getNameIndex(), c.getSignatureIndex(), c.getIndex(), c.getConstantPool());
    }
    
    LocalVariable(final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), constant_pool);
    }
    
    public LocalVariable(final int start_pc, final int length, final int name_index, final int signature_index, final int index, final ConstantPool constant_pool) {
        this.start_pc = start_pc;
        this.length = length;
        this.name_index = name_index;
        this.signature_index = signature_index;
        this.index = index;
        this.constant_pool = constant_pool;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitLocalVariable(this);
    }
    
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeShort(this.start_pc);
        file.writeShort(this.length);
        file.writeShort(this.name_index);
        file.writeShort(this.signature_index);
        file.writeShort(this.index);
    }
    
    public final ConstantPool getConstantPool() {
        return this.constant_pool;
    }
    
    public final int getLength() {
        return this.length;
    }
    
    public final String getName() {
        final ConstantUtf8 c = (ConstantUtf8)this.constant_pool.getConstant(this.name_index, (byte)1);
        return c.getBytes();
    }
    
    public final int getNameIndex() {
        return this.name_index;
    }
    
    public final String getSignature() {
        final ConstantUtf8 c = (ConstantUtf8)this.constant_pool.getConstant(this.signature_index, (byte)1);
        return c.getBytes();
    }
    
    public final int getSignatureIndex() {
        return this.signature_index;
    }
    
    public final int getIndex() {
        return this.index;
    }
    
    public final int getStartPC() {
        return this.start_pc;
    }
    
    public final void setConstantPool(final ConstantPool constant_pool) {
        this.constant_pool = constant_pool;
    }
    
    public final void setLength(final int length) {
        this.length = length;
    }
    
    public final void setNameIndex(final int name_index) {
        this.name_index = name_index;
    }
    
    public final void setSignatureIndex(final int signature_index) {
        this.signature_index = signature_index;
    }
    
    public final void setIndex(final int index) {
        this.index = index;
    }
    
    public final void setStartPC(final int start_pc) {
        this.start_pc = start_pc;
    }
    
    @Override
    public final String toString() {
        final String name = this.getName();
        final String signature = Utility.signatureToString(this.getSignature());
        return "LocalVariable(start_pc = " + this.start_pc + ", length = " + this.length + ", index = " + this.index + ":" + signature + " " + name + ")";
    }
    
    public LocalVariable copy() {
        try {
            return (LocalVariable)this.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
}
