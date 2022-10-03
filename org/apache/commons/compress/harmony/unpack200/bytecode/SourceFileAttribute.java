package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class SourceFileAttribute extends Attribute
{
    private final CPUTF8 name;
    private int nameIndex;
    private static CPUTF8 attributeName;
    
    public static void setAttributeName(final CPUTF8 cpUTF8Value) {
        SourceFileAttribute.attributeName = cpUTF8Value;
    }
    
    public SourceFileAttribute(final CPUTF8 name) {
        super(SourceFileAttribute.attributeName);
        this.name = name;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final SourceFileAttribute other = (SourceFileAttribute)obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean isSourceFileAttribute() {
        return true;
    }
    
    @Override
    protected int getLength() {
        return 2;
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        return new ClassFileEntry[] { this.getAttributeName(), this.name };
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.nameIndex = pool.indexOf(this.name);
    }
    
    @Override
    public String toString() {
        return "SourceFile: " + this.name;
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.nameIndex);
    }
}
