package org.apache.commons.compress.harmony.unpack200.bytecode;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.DataOutputStream;

public class LocalVariableTableAttribute extends BCIRenumberedAttribute
{
    private final int local_variable_table_length;
    private final int[] start_pcs;
    private final int[] lengths;
    private int[] name_indexes;
    private int[] descriptor_indexes;
    private final int[] indexes;
    private final CPUTF8[] names;
    private final CPUTF8[] descriptors;
    private int codeLength;
    private static CPUTF8 attributeName;
    
    public static void setAttributeName(final CPUTF8 cpUTF8Value) {
        LocalVariableTableAttribute.attributeName = cpUTF8Value;
    }
    
    public LocalVariableTableAttribute(final int local_variable_table_length, final int[] start_pcs, final int[] lengths, final CPUTF8[] names, final CPUTF8[] descriptors, final int[] indexes) {
        super(LocalVariableTableAttribute.attributeName);
        this.local_variable_table_length = local_variable_table_length;
        this.start_pcs = start_pcs;
        this.lengths = lengths;
        this.names = names;
        this.descriptors = descriptors;
        this.indexes = indexes;
    }
    
    public void setCodeLength(final int length) {
        this.codeLength = length;
    }
    
    @Override
    protected int getLength() {
        return 2 + 10 * this.local_variable_table_length;
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.local_variable_table_length);
        for (int i = 0; i < this.local_variable_table_length; ++i) {
            dos.writeShort(this.start_pcs[i]);
            dos.writeShort(this.lengths[i]);
            dos.writeShort(this.name_indexes[i]);
            dos.writeShort(this.descriptor_indexes[i]);
            dos.writeShort(this.indexes[i]);
        }
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        final ArrayList nestedEntries = new ArrayList();
        nestedEntries.add(this.getAttributeName());
        for (int i = 0; i < this.local_variable_table_length; ++i) {
            nestedEntries.add(this.names[i]);
            nestedEntries.add(this.descriptors[i]);
        }
        final ClassFileEntry[] nestedEntryArray = new ClassFileEntry[nestedEntries.size()];
        nestedEntries.toArray(nestedEntryArray);
        return nestedEntryArray;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.name_indexes = new int[this.local_variable_table_length];
        this.descriptor_indexes = new int[this.local_variable_table_length];
        for (int i = 0; i < this.local_variable_table_length; ++i) {
            this.names[i].resolve(pool);
            this.descriptors[i].resolve(pool);
            this.name_indexes[i] = pool.indexOf(this.names[i]);
            this.descriptor_indexes[i] = pool.indexOf(this.descriptors[i]);
        }
    }
    
    @Override
    public String toString() {
        return "LocalVariableTable: " + this.local_variable_table_length + " variables";
    }
    
    @Override
    protected int[] getStartPCs() {
        return this.start_pcs;
    }
    
    @Override
    public void renumber(final List byteCodeOffsets) throws Pack200Exception {
        final int[] unrenumbered_start_pcs = new int[this.start_pcs.length];
        System.arraycopy(this.start_pcs, 0, unrenumbered_start_pcs, 0, this.start_pcs.length);
        super.renumber(byteCodeOffsets);
        final int maxSize = this.codeLength;
        for (int index = 0; index < this.lengths.length; ++index) {
            final int start_pc = this.start_pcs[index];
            int revisedLength = -1;
            final int encodedLength = this.lengths[index];
            final int indexOfStartPC = unrenumbered_start_pcs[index];
            final int stopIndex = indexOfStartPC + encodedLength;
            if (stopIndex < 0) {
                throw new Pack200Exception("Error renumbering bytecode indexes");
            }
            if (stopIndex == byteCodeOffsets.size()) {
                revisedLength = maxSize - start_pc;
            }
            else {
                final int stopValue = byteCodeOffsets.get(stopIndex);
                revisedLength = stopValue - start_pc;
            }
            this.lengths[index] = revisedLength;
        }
    }
}
