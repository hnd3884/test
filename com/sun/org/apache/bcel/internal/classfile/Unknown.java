package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.Iterator;
import java.util.HashMap;

public final class Unknown extends Attribute
{
    private byte[] bytes;
    private String name;
    private static HashMap unknown_attributes;
    
    static Unknown[] getUnknownAttributes() {
        final Unknown[] unknowns = new Unknown[Unknown.unknown_attributes.size()];
        final Iterator entries = Unknown.unknown_attributes.values().iterator();
        int i = 0;
        while (entries.hasNext()) {
            unknowns[i] = entries.next();
            ++i;
        }
        Unknown.unknown_attributes.clear();
        return unknowns;
    }
    
    public Unknown(final Unknown c) {
        this(c.getNameIndex(), c.getLength(), c.getBytes(), c.getConstantPool());
    }
    
    public Unknown(final int name_index, final int length, final byte[] bytes, final ConstantPool constant_pool) {
        super((byte)(-1), name_index, length, constant_pool);
        this.bytes = bytes;
        this.name = ((ConstantUtf8)constant_pool.getConstant(name_index, (byte)1)).getBytes();
        Unknown.unknown_attributes.put(this.name, this);
    }
    
    Unknown(final int name_index, final int length, final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, (byte[])null, constant_pool);
        if (length > 0) {
            file.readFully(this.bytes = new byte[length]);
        }
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitUnknown(this);
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
    
    public final String getName() {
        return this.name;
    }
    
    public final void setBytes(final byte[] bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public final String toString() {
        if (this.length == 0 || this.bytes == null) {
            return "(Unknown attribute " + this.name + ")";
        }
        String hex;
        if (this.length > 10) {
            final byte[] tmp = new byte[10];
            System.arraycopy(this.bytes, 0, tmp, 0, 10);
            hex = Utility.toHexString(tmp) + "... (truncated)";
        }
        else {
            hex = Utility.toHexString(this.bytes);
        }
        return "(Unknown attribute " + this.name + ": " + hex + ")";
    }
    
    @Override
    public Attribute copy(final ConstantPool constant_pool) {
        final Unknown c = (Unknown)this.clone();
        if (this.bytes != null) {
            c.bytes = this.bytes.clone();
        }
        c.constant_pool = constant_pool;
        return c;
    }
    
    static {
        Unknown.unknown_attributes = new HashMap();
    }
}
