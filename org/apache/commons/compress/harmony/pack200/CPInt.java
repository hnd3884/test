package org.apache.commons.compress.harmony.pack200;

public class CPInt extends CPConstant
{
    private final int theInt;
    
    public CPInt(final int theInt) {
        this.theInt = theInt;
    }
    
    @Override
    public int compareTo(final Object obj) {
        if (this.theInt > ((CPInt)obj).theInt) {
            return 1;
        }
        if (this.theInt == ((CPInt)obj).theInt) {
            return 0;
        }
        return -1;
    }
    
    public int getInt() {
        return this.theInt;
    }
}
