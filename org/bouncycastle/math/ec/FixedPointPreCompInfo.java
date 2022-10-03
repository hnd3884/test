package org.bouncycastle.math.ec;

public class FixedPointPreCompInfo implements PreCompInfo
{
    protected ECPoint offset;
    @Deprecated
    protected ECPoint[] preComp;
    protected ECLookupTable lookupTable;
    protected int width;
    
    public FixedPointPreCompInfo() {
        this.offset = null;
        this.preComp = null;
        this.lookupTable = null;
        this.width = -1;
    }
    
    public ECLookupTable getLookupTable() {
        return this.lookupTable;
    }
    
    public void setLookupTable(final ECLookupTable lookupTable) {
        this.lookupTable = lookupTable;
    }
    
    public ECPoint getOffset() {
        return this.offset;
    }
    
    public void setOffset(final ECPoint offset) {
        this.offset = offset;
    }
    
    @Deprecated
    public ECPoint[] getPreComp() {
        return this.preComp;
    }
    
    @Deprecated
    public void setPreComp(final ECPoint[] preComp) {
        this.preComp = preComp;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
}
