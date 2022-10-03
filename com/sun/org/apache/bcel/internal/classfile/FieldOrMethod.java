package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public abstract class FieldOrMethod extends AccessFlags implements Cloneable, Node
{
    protected int name_index;
    protected int signature_index;
    protected int attributes_count;
    protected Attribute[] attributes;
    protected ConstantPool constant_pool;
    
    FieldOrMethod() {
    }
    
    protected FieldOrMethod(final FieldOrMethod c) {
        this(c.getAccessFlags(), c.getNameIndex(), c.getSignatureIndex(), c.getAttributes(), c.getConstantPool());
    }
    
    protected FieldOrMethod(final DataInputStream file, final ConstantPool constant_pool) throws IOException, ClassFormatException {
        this(file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), null, constant_pool);
        this.attributes_count = file.readUnsignedShort();
        this.attributes = new Attribute[this.attributes_count];
        for (int i = 0; i < this.attributes_count; ++i) {
            this.attributes[i] = Attribute.readAttribute(file, constant_pool);
        }
    }
    
    protected FieldOrMethod(final int access_flags, final int name_index, final int signature_index, final Attribute[] attributes, final ConstantPool constant_pool) {
        this.access_flags = access_flags;
        this.name_index = name_index;
        this.signature_index = signature_index;
        this.constant_pool = constant_pool;
        this.setAttributes(attributes);
    }
    
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeShort(this.access_flags);
        file.writeShort(this.name_index);
        file.writeShort(this.signature_index);
        file.writeShort(this.attributes_count);
        for (int i = 0; i < this.attributes_count; ++i) {
            this.attributes[i].dump(file);
        }
    }
    
    public final Attribute[] getAttributes() {
        return this.attributes;
    }
    
    public final void setAttributes(final Attribute[] attributes) {
        this.attributes = attributes;
        this.attributes_count = ((attributes == null) ? 0 : attributes.length);
    }
    
    public final ConstantPool getConstantPool() {
        return this.constant_pool;
    }
    
    public final void setConstantPool(final ConstantPool constant_pool) {
        this.constant_pool = constant_pool;
    }
    
    public final int getNameIndex() {
        return this.name_index;
    }
    
    public final void setNameIndex(final int name_index) {
        this.name_index = name_index;
    }
    
    public final int getSignatureIndex() {
        return this.signature_index;
    }
    
    public final void setSignatureIndex(final int signature_index) {
        this.signature_index = signature_index;
    }
    
    public final String getName() {
        final ConstantUtf8 c = (ConstantUtf8)this.constant_pool.getConstant(this.name_index, (byte)1);
        return c.getBytes();
    }
    
    public final String getSignature() {
        final ConstantUtf8 c = (ConstantUtf8)this.constant_pool.getConstant(this.signature_index, (byte)1);
        return c.getBytes();
    }
    
    protected FieldOrMethod copy_(final ConstantPool constant_pool) {
        FieldOrMethod c = null;
        try {
            c = (FieldOrMethod)this.clone();
        }
        catch (final CloneNotSupportedException ex) {}
        c.constant_pool = constant_pool;
        c.attributes = new Attribute[this.attributes_count];
        for (int i = 0; i < this.attributes_count; ++i) {
            c.attributes[i] = this.attributes[i].copy(constant_pool);
        }
        return c;
    }
}
