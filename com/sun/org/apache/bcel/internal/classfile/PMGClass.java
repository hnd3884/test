package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class PMGClass extends Attribute
{
    private int pmg_class_index;
    private int pmg_index;
    
    public PMGClass(final PMGClass c) {
        this(c.getNameIndex(), c.getLength(), c.getPMGIndex(), c.getPMGClassIndex(), c.getConstantPool());
    }
    
    PMGClass(final int name_index, final int length, final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, file.readUnsignedShort(), file.readUnsignedShort(), constant_pool);
    }
    
    public PMGClass(final int name_index, final int length, final int pmg_index, final int pmg_class_index, final ConstantPool constant_pool) {
        super((byte)9, name_index, length, constant_pool);
        this.pmg_index = pmg_index;
        this.pmg_class_index = pmg_class_index;
    }
    
    @Override
    public void accept(final Visitor v) {
        System.err.println("Visiting non-standard PMGClass object");
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.pmg_index);
        file.writeShort(this.pmg_class_index);
    }
    
    public final int getPMGClassIndex() {
        return this.pmg_class_index;
    }
    
    public final void setPMGClassIndex(final int pmg_class_index) {
        this.pmg_class_index = pmg_class_index;
    }
    
    public final int getPMGIndex() {
        return this.pmg_index;
    }
    
    public final void setPMGIndex(final int pmg_index) {
        this.pmg_index = pmg_index;
    }
    
    public final String getPMGName() {
        final ConstantUtf8 c = (ConstantUtf8)this.constant_pool.getConstant(this.pmg_index, (byte)1);
        return c.getBytes();
    }
    
    public final String getPMGClassName() {
        final ConstantUtf8 c = (ConstantUtf8)this.constant_pool.getConstant(this.pmg_class_index, (byte)1);
        return c.getBytes();
    }
    
    @Override
    public final String toString() {
        return "PMGClass(" + this.getPMGName() + ", " + this.getPMGClassName() + ")";
    }
    
    @Override
    public Attribute copy(final ConstantPool constant_pool) {
        return (PMGClass)this.clone();
    }
}
