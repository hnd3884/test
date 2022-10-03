package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class StackMapType implements Cloneable
{
    private byte type;
    private int index;
    private ConstantPool constant_pool;
    
    StackMapType(final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(file.readByte(), -1, constant_pool);
        if (this.hasIndex()) {
            this.setIndex(file.readShort());
        }
        this.setConstantPool(constant_pool);
    }
    
    public StackMapType(final byte type, final int index, final ConstantPool constant_pool) {
        this.index = -1;
        this.setType(type);
        this.setIndex(index);
        this.setConstantPool(constant_pool);
    }
    
    public void setType(final byte t) {
        if (t < 0 || t > 8) {
            throw new RuntimeException("Illegal type for StackMapType: " + t);
        }
        this.type = t;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public void setIndex(final int t) {
        this.index = t;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeByte(this.type);
        if (this.hasIndex()) {
            file.writeShort(this.getIndex());
        }
    }
    
    public final boolean hasIndex() {
        return this.type == 7 || this.type == 8;
    }
    
    private String printIndex() {
        if (this.type == 7) {
            return ", class=" + this.constant_pool.constantToString(this.index, (byte)7);
        }
        if (this.type == 8) {
            return ", offset=" + this.index;
        }
        return "";
    }
    
    @Override
    public final String toString() {
        return "(type=" + Constants.ITEM_NAMES[this.type] + this.printIndex() + ")";
    }
    
    public StackMapType copy() {
        try {
            return (StackMapType)this.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public final ConstantPool getConstantPool() {
        return this.constant_pool;
    }
    
    public final void setConstantPool(final ConstantPool constant_pool) {
        this.constant_pool = constant_pool;
    }
}
