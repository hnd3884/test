package org.apache.commons.compress.harmony.pack200;

public class CPLong extends CPConstant
{
    private final long theLong;
    
    public CPLong(final long theLong) {
        this.theLong = theLong;
    }
    
    @Override
    public int compareTo(final Object obj) {
        if (this.theLong > ((CPLong)obj).theLong) {
            return 1;
        }
        if (this.theLong == ((CPLong)obj).theLong) {
            return 0;
        }
        return -1;
    }
    
    public long getLong() {
        return this.theLong;
    }
    
    @Override
    public String toString() {
        return "" + this.theLong;
    }
}
