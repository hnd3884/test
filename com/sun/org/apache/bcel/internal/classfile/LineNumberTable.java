package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class LineNumberTable extends Attribute
{
    private int line_number_table_length;
    private LineNumber[] line_number_table;
    
    public LineNumberTable(final LineNumberTable c) {
        this(c.getNameIndex(), c.getLength(), c.getLineNumberTable(), c.getConstantPool());
    }
    
    public LineNumberTable(final int name_index, final int length, final LineNumber[] line_number_table, final ConstantPool constant_pool) {
        super((byte)4, name_index, length, constant_pool);
        this.setLineNumberTable(line_number_table);
    }
    
    LineNumberTable(final int name_index, final int length, final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, (LineNumber[])null, constant_pool);
        this.line_number_table_length = file.readUnsignedShort();
        this.line_number_table = new LineNumber[this.line_number_table_length];
        for (int i = 0; i < this.line_number_table_length; ++i) {
            this.line_number_table[i] = new LineNumber(file);
        }
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitLineNumberTable(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.line_number_table_length);
        for (int i = 0; i < this.line_number_table_length; ++i) {
            this.line_number_table[i].dump(file);
        }
    }
    
    public final LineNumber[] getLineNumberTable() {
        return this.line_number_table;
    }
    
    public final void setLineNumberTable(final LineNumber[] line_number_table) {
        this.line_number_table = line_number_table;
        this.line_number_table_length = ((line_number_table == null) ? 0 : line_number_table.length);
    }
    
    @Override
    public final String toString() {
        final StringBuffer buf = new StringBuffer();
        final StringBuffer line = new StringBuffer();
        for (int i = 0; i < this.line_number_table_length; ++i) {
            line.append(this.line_number_table[i].toString());
            if (i < this.line_number_table_length - 1) {
                line.append(", ");
            }
            if (line.length() > 72) {
                line.append('\n');
                buf.append(line);
                line.setLength(0);
            }
        }
        buf.append(line);
        return buf.toString();
    }
    
    public int getSourceLine(final int pos) {
        int l = 0;
        int r = this.line_number_table_length - 1;
        if (r < 0) {
            return -1;
        }
        int min_index = -1;
        int min = -1;
        do {
            final int i = (l + r) / 2;
            final int j = this.line_number_table[i].getStartPC();
            if (j == pos) {
                return this.line_number_table[i].getLineNumber();
            }
            if (pos < j) {
                r = i - 1;
            }
            else {
                l = i + 1;
            }
            if (j >= pos || j <= min) {
                continue;
            }
            min = j;
            min_index = i;
        } while (l <= r);
        if (min_index < 0) {
            return -1;
        }
        return this.line_number_table[min_index].getLineNumber();
    }
    
    @Override
    public Attribute copy(final ConstantPool constant_pool) {
        final LineNumberTable c = (LineNumberTable)this.clone();
        c.line_number_table = new LineNumber[this.line_number_table_length];
        for (int i = 0; i < this.line_number_table_length; ++i) {
            c.line_number_table[i] = this.line_number_table[i].copy();
        }
        c.constant_pool = constant_pool;
        return c;
    }
    
    public final int getTableLength() {
        return this.line_number_table_length;
    }
}
