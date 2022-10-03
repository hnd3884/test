package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class ConstantValueAttribute extends Attribute
{
    private int constantIndex;
    private final ClassFileEntry entry;
    private static CPUTF8 attributeName;
    
    public static void setAttributeName(final CPUTF8 cpUTF8Value) {
        ConstantValueAttribute.attributeName = cpUTF8Value;
    }
    
    public ConstantValueAttribute(final ClassFileEntry entry) {
        super(ConstantValueAttribute.attributeName);
        if (entry == null) {
            throw new NullPointerException();
        }
        this.entry = entry;
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
        final ConstantValueAttribute other = (ConstantValueAttribute)obj;
        if (this.entry == null) {
            if (other.entry != null) {
                return false;
            }
        }
        else if (!this.entry.equals(other.entry)) {
            return false;
        }
        return true;
    }
    
    @Override
    protected int getLength() {
        return 2;
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        return new ClassFileEntry[] { this.getAttributeName(), this.entry };
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.entry == null) ? 0 : this.entry.hashCode());
        return result;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.entry.resolve(pool);
        this.constantIndex = pool.indexOf(this.entry);
    }
    
    @Override
    public String toString() {
        return "Constant:" + this.entry;
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.constantIndex);
    }
}
