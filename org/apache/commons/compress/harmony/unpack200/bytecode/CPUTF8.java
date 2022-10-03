package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class CPUTF8 extends ConstantPoolEntry
{
    private final String utf8;
    private boolean hashcodeComputed;
    private int cachedHashCode;
    
    public CPUTF8(final String utf8, final int globalIndex) {
        super((byte)1, globalIndex);
        this.utf8 = utf8;
        if (utf8 == null) {
            throw new NullPointerException("Null arguments are not allowed");
        }
    }
    
    public CPUTF8(final String string) {
        this(string, -1);
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
        final CPUTF8 other = (CPUTF8)obj;
        return this.utf8.equals(other.utf8);
    }
    
    private void generateHashCode() {
        this.hashcodeComputed = true;
        final int PRIME = 31;
        this.cachedHashCode = 31 + this.utf8.hashCode();
    }
    
    @Override
    public int hashCode() {
        if (!this.hashcodeComputed) {
            this.generateHashCode();
        }
        return this.cachedHashCode;
    }
    
    @Override
    public String toString() {
        return "UTF8: " + this.utf8;
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeUTF(this.utf8);
    }
    
    public String underlyingString() {
        return this.utf8;
    }
    
    public void setGlobalIndex(final int index) {
        this.globalIndex = index;
    }
}
