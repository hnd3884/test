package org.apache.commons.compress.harmony.pack200;

public class CPFloat extends CPConstant
{
    private final float theFloat;
    
    public CPFloat(final float theFloat) {
        this.theFloat = theFloat;
    }
    
    @Override
    public int compareTo(final Object obj) {
        return Float.compare(this.theFloat, ((CPFloat)obj).theFloat);
    }
    
    public float getFloat() {
        return this.theFloat;
    }
}
