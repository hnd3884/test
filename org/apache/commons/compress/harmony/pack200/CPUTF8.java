package org.apache.commons.compress.harmony.pack200;

public class CPUTF8 extends ConstantPoolEntry implements Comparable
{
    private final String string;
    
    public CPUTF8(final String string) {
        this.string = string;
    }
    
    @Override
    public int compareTo(final Object arg0) {
        return this.string.compareTo(((CPUTF8)arg0).string);
    }
    
    @Override
    public String toString() {
        return this.string;
    }
    
    public String getUnderlyingString() {
        return this.string;
    }
}
