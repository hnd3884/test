package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class ExceptionTable extends Attribute
{
    private int number_of_exceptions;
    private int[] exception_index_table;
    
    public ExceptionTable(final ExceptionTable c) {
        this(c.getNameIndex(), c.getLength(), c.getExceptionIndexTable(), c.getConstantPool());
    }
    
    public ExceptionTable(final int name_index, final int length, final int[] exception_index_table, final ConstantPool constant_pool) {
        super((byte)3, name_index, length, constant_pool);
        this.setExceptionIndexTable(exception_index_table);
    }
    
    ExceptionTable(final int name_index, final int length, final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, (int[])null, constant_pool);
        this.number_of_exceptions = file.readUnsignedShort();
        this.exception_index_table = new int[this.number_of_exceptions];
        for (int i = 0; i < this.number_of_exceptions; ++i) {
            this.exception_index_table[i] = file.readUnsignedShort();
        }
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitExceptionTable(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.number_of_exceptions);
        for (int i = 0; i < this.number_of_exceptions; ++i) {
            file.writeShort(this.exception_index_table[i]);
        }
    }
    
    public final int[] getExceptionIndexTable() {
        return this.exception_index_table;
    }
    
    public final int getNumberOfExceptions() {
        return this.number_of_exceptions;
    }
    
    public final String[] getExceptionNames() {
        final String[] names = new String[this.number_of_exceptions];
        for (int i = 0; i < this.number_of_exceptions; ++i) {
            names[i] = this.constant_pool.getConstantString(this.exception_index_table[i], (byte)7).replace('/', '.');
        }
        return names;
    }
    
    public final void setExceptionIndexTable(final int[] exception_index_table) {
        this.exception_index_table = exception_index_table;
        this.number_of_exceptions = ((exception_index_table == null) ? 0 : exception_index_table.length);
    }
    
    @Override
    public final String toString() {
        final StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < this.number_of_exceptions; ++i) {
            final String str = this.constant_pool.getConstantString(this.exception_index_table[i], (byte)7);
            buf.append(Utility.compactClassName(str, false));
            if (i < this.number_of_exceptions - 1) {
                buf.append(", ");
            }
        }
        return buf.toString();
    }
    
    @Override
    public Attribute copy(final ConstantPool constant_pool) {
        final ExceptionTable c = (ExceptionTable)this.clone();
        c.exception_index_table = this.exception_index_table.clone();
        c.constant_pool = constant_pool;
        return c;
    }
}
