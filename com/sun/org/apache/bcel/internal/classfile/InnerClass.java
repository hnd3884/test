package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class InnerClass implements Cloneable, Node
{
    private int inner_class_index;
    private int outer_class_index;
    private int inner_name_index;
    private int inner_access_flags;
    
    public InnerClass(final InnerClass c) {
        this(c.getInnerClassIndex(), c.getOuterClassIndex(), c.getInnerNameIndex(), c.getInnerAccessFlags());
    }
    
    InnerClass(final DataInputStream file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort());
    }
    
    public InnerClass(final int inner_class_index, final int outer_class_index, final int inner_name_index, final int inner_access_flags) {
        this.inner_class_index = inner_class_index;
        this.outer_class_index = outer_class_index;
        this.inner_name_index = inner_name_index;
        this.inner_access_flags = inner_access_flags;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitInnerClass(this);
    }
    
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeShort(this.inner_class_index);
        file.writeShort(this.outer_class_index);
        file.writeShort(this.inner_name_index);
        file.writeShort(this.inner_access_flags);
    }
    
    public final int getInnerAccessFlags() {
        return this.inner_access_flags;
    }
    
    public final int getInnerClassIndex() {
        return this.inner_class_index;
    }
    
    public final int getInnerNameIndex() {
        return this.inner_name_index;
    }
    
    public final int getOuterClassIndex() {
        return this.outer_class_index;
    }
    
    public final void setInnerAccessFlags(final int inner_access_flags) {
        this.inner_access_flags = inner_access_flags;
    }
    
    public final void setInnerClassIndex(final int inner_class_index) {
        this.inner_class_index = inner_class_index;
    }
    
    public final void setInnerNameIndex(final int inner_name_index) {
        this.inner_name_index = inner_name_index;
    }
    
    public final void setOuterClassIndex(final int outer_class_index) {
        this.outer_class_index = outer_class_index;
    }
    
    @Override
    public final String toString() {
        return "InnerClass(" + this.inner_class_index + ", " + this.outer_class_index + ", " + this.inner_name_index + ", " + this.inner_access_flags + ")";
    }
    
    public final String toString(final ConstantPool constant_pool) {
        String inner_class_name = constant_pool.getConstantString(this.inner_class_index, (byte)7);
        inner_class_name = Utility.compactClassName(inner_class_name);
        String outer_class_name;
        if (this.outer_class_index != 0) {
            outer_class_name = constant_pool.getConstantString(this.outer_class_index, (byte)7);
            outer_class_name = Utility.compactClassName(outer_class_name);
        }
        else {
            outer_class_name = "<not a member>";
        }
        String inner_name;
        if (this.inner_name_index != 0) {
            inner_name = ((ConstantUtf8)constant_pool.getConstant(this.inner_name_index, (byte)1)).getBytes();
        }
        else {
            inner_name = "<anonymous>";
        }
        String access = Utility.accessToString(this.inner_access_flags, true);
        access = (access.equals("") ? "" : (access + " "));
        return "InnerClass:" + access + inner_class_name + "(\"" + outer_class_name + "\", \"" + inner_name + "\")";
    }
    
    public InnerClass copy() {
        try {
            return (InnerClass)this.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
}
