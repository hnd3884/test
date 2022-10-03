package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public abstract class ClassFileEntry
{
    protected static final ClassFileEntry[] NONE;
    private boolean resolved;
    
    protected abstract void doWrite(final DataOutputStream p0) throws IOException;
    
    @Override
    public abstract boolean equals(final Object p0);
    
    protected ClassFileEntry[] getNestedClassFileEntries() {
        return ClassFileEntry.NONE;
    }
    
    @Override
    public abstract int hashCode();
    
    protected void resolve(final ClassConstantPool pool) {
        this.resolved = true;
    }
    
    protected int objectHashCode() {
        return super.hashCode();
    }
    
    @Override
    public abstract String toString();
    
    public final void write(final DataOutputStream dos) throws IOException {
        if (!this.resolved) {
            throw new IllegalStateException("Entry has not been resolved");
        }
        this.doWrite(dos);
    }
    
    static {
        NONE = new ClassFileEntry[0];
    }
}
