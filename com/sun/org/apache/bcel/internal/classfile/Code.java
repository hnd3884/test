package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public final class Code extends Attribute
{
    private int max_stack;
    private int max_locals;
    private int code_length;
    private byte[] code;
    private int exception_table_length;
    private CodeException[] exception_table;
    private int attributes_count;
    private Attribute[] attributes;
    
    public Code(final Code c) {
        this(c.getNameIndex(), c.getLength(), c.getMaxStack(), c.getMaxLocals(), c.getCode(), c.getExceptionTable(), c.getAttributes(), c.getConstantPool());
    }
    
    Code(final int name_index, final int length, final DataInputStream file, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, file.readUnsignedShort(), file.readUnsignedShort(), null, null, null, constant_pool);
        this.code_length = file.readInt();
        file.readFully(this.code = new byte[this.code_length]);
        this.exception_table_length = file.readUnsignedShort();
        this.exception_table = new CodeException[this.exception_table_length];
        for (int i = 0; i < this.exception_table_length; ++i) {
            this.exception_table[i] = new CodeException(file);
        }
        this.attributes_count = file.readUnsignedShort();
        this.attributes = new Attribute[this.attributes_count];
        for (int i = 0; i < this.attributes_count; ++i) {
            this.attributes[i] = Attribute.readAttribute(file, constant_pool);
        }
        this.length = length;
    }
    
    public Code(final int name_index, final int length, final int max_stack, final int max_locals, final byte[] code, final CodeException[] exception_table, final Attribute[] attributes, final ConstantPool constant_pool) {
        super((byte)2, name_index, length, constant_pool);
        this.max_stack = max_stack;
        this.max_locals = max_locals;
        this.setCode(code);
        this.setExceptionTable(exception_table);
        this.setAttributes(attributes);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitCode(this);
    }
    
    @Override
    public final void dump(final DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.max_stack);
        file.writeShort(this.max_locals);
        file.writeInt(this.code_length);
        file.write(this.code, 0, this.code_length);
        file.writeShort(this.exception_table_length);
        for (int i = 0; i < this.exception_table_length; ++i) {
            this.exception_table[i].dump(file);
        }
        file.writeShort(this.attributes_count);
        for (int i = 0; i < this.attributes_count; ++i) {
            this.attributes[i].dump(file);
        }
    }
    
    public final Attribute[] getAttributes() {
        return this.attributes;
    }
    
    public LineNumberTable getLineNumberTable() {
        for (int i = 0; i < this.attributes_count; ++i) {
            if (this.attributes[i] instanceof LineNumberTable) {
                return (LineNumberTable)this.attributes[i];
            }
        }
        return null;
    }
    
    public LocalVariableTable getLocalVariableTable() {
        for (int i = 0; i < this.attributes_count; ++i) {
            if (this.attributes[i] instanceof LocalVariableTable) {
                return (LocalVariableTable)this.attributes[i];
            }
        }
        return null;
    }
    
    public final byte[] getCode() {
        return this.code;
    }
    
    public final CodeException[] getExceptionTable() {
        return this.exception_table;
    }
    
    public final int getMaxLocals() {
        return this.max_locals;
    }
    
    public final int getMaxStack() {
        return this.max_stack;
    }
    
    private final int getInternalLength() {
        return 8 + this.code_length + 2 + 8 * this.exception_table_length + 2;
    }
    
    private final int calculateLength() {
        int len = 0;
        for (int i = 0; i < this.attributes_count; ++i) {
            len += this.attributes[i].length + 6;
        }
        return len + this.getInternalLength();
    }
    
    public final void setAttributes(final Attribute[] attributes) {
        this.attributes = attributes;
        this.attributes_count = ((attributes == null) ? 0 : attributes.length);
        this.length = this.calculateLength();
    }
    
    public final void setCode(final byte[] code) {
        this.code = code;
        this.code_length = ((code == null) ? 0 : code.length);
    }
    
    public final void setExceptionTable(final CodeException[] exception_table) {
        this.exception_table = exception_table;
        this.exception_table_length = ((exception_table == null) ? 0 : exception_table.length);
    }
    
    public final void setMaxLocals(final int max_locals) {
        this.max_locals = max_locals;
    }
    
    public final void setMaxStack(final int max_stack) {
        this.max_stack = max_stack;
    }
    
    public final String toString(final boolean verbose) {
        final StringBuffer buf = new StringBuffer("Code(max_stack = " + this.max_stack + ", max_locals = " + this.max_locals + ", code_length = " + this.code_length + ")\n" + Utility.codeToString(this.code, this.constant_pool, 0, -1, verbose));
        if (this.exception_table_length > 0) {
            buf.append("\nException handler(s) = \nFrom\tTo\tHandler\tType\n");
            for (int i = 0; i < this.exception_table_length; ++i) {
                buf.append(this.exception_table[i].toString(this.constant_pool, verbose) + "\n");
            }
        }
        if (this.attributes_count > 0) {
            buf.append("\nAttribute(s) = \n");
            for (int i = 0; i < this.attributes_count; ++i) {
                buf.append(this.attributes[i].toString() + "\n");
            }
        }
        return buf.toString();
    }
    
    @Override
    public final String toString() {
        return this.toString(true);
    }
    
    @Override
    public Attribute copy(final ConstantPool constant_pool) {
        final Code c = (Code)this.clone();
        c.code = this.code.clone();
        c.constant_pool = constant_pool;
        c.exception_table = new CodeException[this.exception_table_length];
        for (int i = 0; i < this.exception_table_length; ++i) {
            c.exception_table[i] = this.exception_table[i].copy();
        }
        c.attributes = new Attribute[this.attributes_count];
        for (int i = 0; i < this.attributes_count; ++i) {
            c.attributes[i] = this.attributes[i].copy(constant_pool);
        }
        return c;
    }
}
