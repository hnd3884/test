package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public abstract class Attribute extends ClassFileEntry
{
    protected final CPUTF8 attributeName;
    private int attributeNameIndex;
    
    public Attribute(final CPUTF8 attributeName) {
        this.attributeName = attributeName;
    }
    
    @Override
    protected void doWrite(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.attributeNameIndex);
        dos.writeInt(this.getLength());
        this.writeBody(dos);
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
        final Attribute other = (Attribute)obj;
        if (this.attributeName == null) {
            if (other.attributeName != null) {
                return false;
            }
        }
        else if (!this.attributeName.equals(other.attributeName)) {
            return false;
        }
        return true;
    }
    
    protected CPUTF8 getAttributeName() {
        return this.attributeName;
    }
    
    protected abstract int getLength();
    
    protected int getLengthIncludingHeader() {
        return this.getLength() + 2 + 4;
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        return new ClassFileEntry[] { this.getAttributeName() };
    }
    
    public boolean hasBCIRenumbering() {
        return false;
    }
    
    public boolean isSourceFileAttribute() {
        return false;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = 31 * result + ((this.attributeName == null) ? 0 : this.attributeName.hashCode());
        return result;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.attributeNameIndex = pool.indexOf(this.attributeName);
    }
    
    protected abstract void writeBody(final DataOutputStream p0) throws IOException;
}
