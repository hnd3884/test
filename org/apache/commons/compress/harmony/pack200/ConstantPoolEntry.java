package org.apache.commons.compress.harmony.pack200;

public abstract class ConstantPoolEntry
{
    private int index;
    
    public ConstantPoolEntry() {
        this.index = -1;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
}
