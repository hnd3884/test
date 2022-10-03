package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class LineNumberTableAttribute extends BCIRenumberedAttribute
{
    private final int line_number_table_length;
    private final int[] start_pcs;
    private final int[] line_numbers;
    private static CPUTF8 attributeName;
    
    public static void setAttributeName(final CPUTF8 cpUTF8Value) {
        LineNumberTableAttribute.attributeName = cpUTF8Value;
    }
    
    public LineNumberTableAttribute(final int line_number_table_length, final int[] start_pcs, final int[] line_numbers) {
        super(LineNumberTableAttribute.attributeName);
        this.line_number_table_length = line_number_table_length;
        this.start_pcs = start_pcs;
        this.line_numbers = line_numbers;
    }
    
    @Override
    protected int getLength() {
        return 2 + 4 * this.line_number_table_length;
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.line_number_table_length);
        for (int i = 0; i < this.line_number_table_length; ++i) {
            dos.writeShort(this.start_pcs[i]);
            dos.writeShort(this.line_numbers[i]);
        }
    }
    
    @Override
    public String toString() {
        return "LineNumberTable: " + this.line_number_table_length + " lines";
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        return new ClassFileEntry[] { this.getAttributeName() };
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
    }
    
    @Override
    protected int[] getStartPCs() {
        return this.start_pcs;
    }
}
