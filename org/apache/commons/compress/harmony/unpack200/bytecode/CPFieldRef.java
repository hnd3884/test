package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class CPFieldRef extends ConstantPoolEntry
{
    CPClass className;
    transient int classNameIndex;
    private final CPNameAndType nameAndType;
    transient int nameAndTypeIndex;
    private boolean hashcodeComputed;
    private int cachedHashCode;
    
    public CPFieldRef(final CPClass className, final CPNameAndType descriptor, final int globalIndex) {
        super((byte)9, globalIndex);
        this.className = className;
        this.nameAndType = descriptor;
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        return new ClassFileEntry[] { this.className, this.nameAndType };
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.nameAndTypeIndex = pool.indexOf(this.nameAndType);
        this.classNameIndex = pool.indexOf(this.className);
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.classNameIndex);
        dos.writeShort(this.nameAndTypeIndex);
    }
    
    @Override
    public String toString() {
        return "FieldRef: " + this.className + "#" + this.nameAndType;
    }
    
    private void generateHashCode() {
        this.hashcodeComputed = true;
        final int PRIME = 31;
        int result = 1;
        result = 31 * result + ((this.className == null) ? 0 : this.className.hashCode());
        result = 31 * result + ((this.nameAndType == null) ? 0 : this.nameAndType.hashCode());
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
        final CPFieldRef other = (CPFieldRef)obj;
        if (this.className == null) {
            if (other.className != null) {
                return false;
            }
        }
        else if (!this.className.equals(other.className)) {
            return false;
        }
        if (this.nameAndType == null) {
            if (other.nameAndType != null) {
                return false;
            }
        }
        else if (!this.nameAndType.equals(other.nameAndType)) {
            return false;
        }
        return true;
    }
}
