package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public abstract class ConstantCP extends Constant
{
    protected int class_index;
    protected int name_and_type_index;
    
    public ConstantCP(final ConstantCP c) {
        this(c.getTag(), c.getClassIndex(), c.getNameAndTypeIndex());
    }
    
    ConstantCP(final byte tag, final DataInputStream file) throws IOException {
        this(tag, file.readUnsignedShort(), file.readUnsignedShort());
    }
    
    protected ConstantCP(final byte tag, final int class_index, final int name_and_type_index) {
        super(tag);
        this.class_index = class_index;
        this.name_and_type_index = name_and_type_index;
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeShort(this.class_index);
        file.writeShort(this.name_and_type_index);
    }
    
    public final int getClassIndex() {
        return this.class_index;
    }
    
    public final int getNameAndTypeIndex() {
        return this.name_and_type_index;
    }
    
    public final void setClassIndex(final int class_index) {
        this.class_index = class_index;
    }
    
    public String getClass(final ConstantPool cp) {
        return cp.constantToString(this.class_index, (byte)7);
    }
    
    public final void setNameAndTypeIndex(final int name_and_type_index) {
        this.name_and_type_index = name_and_type_index;
    }
    
    @Override
    public final String toString() {
        return super.toString() + "(class_index = " + this.class_index + ", name_and_type_index = " + this.name_and_type_index + ")";
    }
}
