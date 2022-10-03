package org.apache.commons.compress.harmony.unpack200.bytecode;

import org.apache.commons.compress.harmony.unpack200.SegmentUtils;
import java.io.IOException;
import java.io.DataOutputStream;

public class CPNameAndType extends ConstantPoolEntry
{
    CPUTF8 descriptor;
    transient int descriptorIndex;
    CPUTF8 name;
    transient int nameIndex;
    private boolean hashcodeComputed;
    private int cachedHashCode;
    
    public CPNameAndType(final CPUTF8 name, final CPUTF8 descriptor, final int globalIndex) {
        super((byte)12, globalIndex);
        this.name = name;
        this.descriptor = descriptor;
        if (name == null || descriptor == null) {
            throw new NullPointerException("Null arguments are not allowed");
        }
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        return new ClassFileEntry[] { this.name, this.descriptor };
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.descriptorIndex = pool.indexOf(this.descriptor);
        this.nameIndex = pool.indexOf(this.name);
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.nameIndex);
        dos.writeShort(this.descriptorIndex);
    }
    
    @Override
    public String toString() {
        return "NameAndType: " + this.name + "(" + this.descriptor + ")";
    }
    
    private void generateHashCode() {
        this.hashcodeComputed = true;
        final int PRIME = 31;
        int result = 1;
        result = 31 * result + this.descriptor.hashCode();
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
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CPNameAndType other = (CPNameAndType)obj;
        return this.descriptor.equals(other.descriptor) && this.name.equals(other.name);
    }
    
    public int invokeInterfaceCount() {
        return 1 + SegmentUtils.countInvokeInterfaceArgs(this.descriptor.underlyingString());
    }
}
