package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class CPString extends CPConstant
{
    private transient int nameIndex;
    private final CPUTF8 name;
    private boolean hashcodeComputed;
    private int cachedHashCode;
    
    public CPString(final CPUTF8 value, final int globalIndex) {
        super((byte)8, value, globalIndex);
        this.name = value;
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.nameIndex);
    }
    
    @Override
    public String toString() {
        return "String: " + this.getValue();
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.nameIndex = pool.indexOf(this.name);
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        return new ClassFileEntry[] { this.name };
    }
    
    private void generateHashCode() {
        this.hashcodeComputed = true;
        final int PRIME = 31;
        int result = 1;
        result = 31 * result + this.name.hashCode();
        this.cachedHashCode = result;
    }
    
    @Override
    public int hashCode() {
        if (!this.hashcodeComputed) {
            this.generateHashCode();
        }
        return this.cachedHashCode;
    }
}
