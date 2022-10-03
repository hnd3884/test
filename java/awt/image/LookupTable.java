package java.awt.image;

public abstract class LookupTable
{
    int numComponents;
    int offset;
    int numEntries;
    
    protected LookupTable(final int offset, final int numComponents) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be greater than 0");
        }
        if (numComponents < 1) {
            throw new IllegalArgumentException("Number of components must  be at least 1");
        }
        this.numComponents = numComponents;
        this.offset = offset;
    }
    
    public int getNumComponents() {
        return this.numComponents;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public abstract int[] lookupPixel(final int[] p0, final int[] p1);
}
