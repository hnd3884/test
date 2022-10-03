package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.List;

public class CPMethod extends CPMember
{
    private boolean hashcodeComputed;
    private int cachedHashCode;
    
    public CPMethod(final CPUTF8 name, final CPUTF8 descriptor, final long flags, final List attributes) {
        super(name, descriptor, flags, attributes);
    }
    
    @Override
    public String toString() {
        return "Method: " + this.name + "(" + this.descriptor + ")";
    }
    
    private void generateHashCode() {
        this.hashcodeComputed = true;
        final int PRIME = 31;
        int result = 1;
        result = 31 * result + this.name.hashCode();
        result = 31 * result + this.descriptor.hashCode();
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
