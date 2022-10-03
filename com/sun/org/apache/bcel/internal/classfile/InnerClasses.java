package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class InnerClasses extends Attribute
{
    private InnerClass[] inner_classes;
    private int number_of_classes;
    
    public InnerClasses(final InnerClasses c) {
        this(c.getNameIndex(), c.getLength(), c.getInnerClasses(), c.getConstantPool());
    }
    
    public InnerClasses(final int name_index, final int length, final InnerClass[] inner_classes, final ConstantPool constant_pool) {
        super((byte)6, name_index, length, constant_pool);
        this.setInnerClasses(inner_classes);
    }
    
    InnerClasses(final int name_index, final int length, final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, (InnerClass[])null, constant_pool);
        this.number_of_classes = file.readUnsignedShort();
        this.inner_classes = new InnerClass[this.number_of_classes];
        for (int i = 0; i < this.number_of_classes; ++i) {
            this.inner_classes[i] = new InnerClass(file);
        }
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitInnerClasses(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.number_of_classes);
        for (int i = 0; i < this.number_of_classes; ++i) {
            this.inner_classes[i].dump(file);
        }
    }
    
    public final InnerClass[] getInnerClasses() {
        return this.inner_classes;
    }
    
    public final void setInnerClasses(final InnerClass[] inner_classes) {
        this.inner_classes = inner_classes;
        this.number_of_classes = ((inner_classes == null) ? 0 : inner_classes.length);
    }
    
    @Override
    public final String toString() {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.number_of_classes; ++i) {
            buf.append(this.inner_classes[i].toString(this.constant_pool) + "\n");
        }
        return buf.toString();
    }
    
    @Override
    public Attribute copy(final ConstantPool constant_pool) {
        final InnerClasses c = (InnerClasses)this.clone();
        c.inner_classes = new InnerClass[this.number_of_classes];
        for (int i = 0; i < this.number_of_classes; ++i) {
            c.inner_classes[i] = this.inner_classes[i].copy();
        }
        c.constant_pool = constant_pool;
        return c;
    }
}
