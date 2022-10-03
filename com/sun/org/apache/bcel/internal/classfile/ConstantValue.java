package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantValue extends Attribute
{
    private int constantvalue_index;
    
    public ConstantValue(final ConstantValue c) {
        this(c.getNameIndex(), c.getLength(), c.getConstantValueIndex(), c.getConstantPool());
    }
    
    ConstantValue(final int name_index, final int length, final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, file.readUnsignedShort(), constant_pool);
    }
    
    public ConstantValue(final int name_index, final int length, final int constantvalue_index, final ConstantPool constant_pool) {
        super((byte)1, name_index, length, constant_pool);
        this.constantvalue_index = constantvalue_index;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantValue(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.constantvalue_index);
    }
    
    public final int getConstantValueIndex() {
        return this.constantvalue_index;
    }
    
    public final void setConstantValueIndex(final int constantvalue_index) {
        this.constantvalue_index = constantvalue_index;
    }
    
    @Override
    public final String toString() {
        Constant c = this.constant_pool.getConstant(this.constantvalue_index);
        String buf = null;
        switch (c.getTag()) {
            case 5: {
                buf = "" + ((ConstantLong)c).getBytes();
                break;
            }
            case 4: {
                buf = "" + ((ConstantFloat)c).getBytes();
                break;
            }
            case 6: {
                buf = "" + ((ConstantDouble)c).getBytes();
                break;
            }
            case 3: {
                buf = "" + ((ConstantInteger)c).getBytes();
                break;
            }
            case 8: {
                final int i = ((ConstantString)c).getStringIndex();
                c = this.constant_pool.getConstant(i, (byte)1);
                buf = "\"" + Utility.convertString(((ConstantUtf8)c).getBytes()) + "\"";
                break;
            }
            default: {
                throw new IllegalStateException("Type of ConstValue invalid: " + c);
            }
        }
        return buf;
    }
    
    @Override
    public Attribute copy(final ConstantPool constant_pool) {
        final ConstantValue c = (ConstantValue)this.clone();
        c.constant_pool = constant_pool;
        return c;
    }
}
