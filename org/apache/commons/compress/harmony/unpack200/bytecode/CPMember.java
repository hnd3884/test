package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;
import java.util.Collections;
import java.util.List;

public class CPMember extends ClassFileEntry
{
    List attributes;
    short flags;
    CPUTF8 name;
    transient int nameIndex;
    protected final CPUTF8 descriptor;
    transient int descriptorIndex;
    
    public CPMember(final CPUTF8 name, final CPUTF8 descriptor, final long flags, final List attributes) {
        this.name = name;
        this.descriptor = descriptor;
        this.flags = (short)flags;
        this.attributes = ((attributes == null) ? Collections.EMPTY_LIST : attributes);
        if (name == null || descriptor == null) {
            throw new NullPointerException("Null arguments are not allowed");
        }
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        final int attributeCount = this.attributes.size();
        final ClassFileEntry[] entries = new ClassFileEntry[attributeCount + 2];
        entries[0] = this.name;
        entries[1] = this.descriptor;
        for (int i = 0; i < attributeCount; ++i) {
            entries[i + 2] = this.attributes.get(i);
        }
        return entries;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.nameIndex = pool.indexOf(this.name);
        this.descriptorIndex = pool.indexOf(this.descriptor);
        for (int it = 0; it < this.attributes.size(); ++it) {
            final Attribute attribute = this.attributes.get(it);
            attribute.resolve(pool);
        }
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = 31 * result + this.attributes.hashCode();
        result = 31 * result + this.descriptor.hashCode();
        result = 31 * result + this.flags;
        result = 31 * result + this.name.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "CPMember: " + this.name + "(" + this.descriptor + ")";
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
        final CPMember other = (CPMember)obj;
        return this.attributes.equals(other.attributes) && this.descriptor.equals(other.descriptor) && this.flags == other.flags && this.name.equals(other.name);
    }
    
    @Override
    protected void doWrite(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.flags);
        dos.writeShort(this.nameIndex);
        dos.writeShort(this.descriptorIndex);
        final int attributeCount = this.attributes.size();
        dos.writeShort(attributeCount);
        for (int i = 0; i < attributeCount; ++i) {
            final Attribute attribute = this.attributes.get(i);
            attribute.doWrite(dos);
        }
    }
}
