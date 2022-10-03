package sun.java2d.pipe;

public class RegionIterator
{
    Region region;
    int curIndex;
    int numXbands;
    
    RegionIterator(final Region region) {
        this.region = region;
    }
    
    public RegionIterator createCopy() {
        final RegionIterator regionIterator = new RegionIterator(this.region);
        regionIterator.curIndex = this.curIndex;
        regionIterator.numXbands = this.numXbands;
        return regionIterator;
    }
    
    public void copyStateFrom(final RegionIterator regionIterator) {
        if (this.region != regionIterator.region) {
            throw new InternalError("region mismatch");
        }
        this.curIndex = regionIterator.curIndex;
        this.numXbands = regionIterator.numXbands;
    }
    
    public boolean nextYRange(final int[] array) {
        this.curIndex += this.numXbands * 2;
        this.numXbands = 0;
        if (this.curIndex >= this.region.endIndex) {
            return false;
        }
        array[1] = this.region.bands[this.curIndex++];
        array[3] = this.region.bands[this.curIndex++];
        this.numXbands = this.region.bands[this.curIndex++];
        return true;
    }
    
    public boolean nextXBand(final int[] array) {
        if (this.numXbands <= 0) {
            return false;
        }
        --this.numXbands;
        array[0] = this.region.bands[this.curIndex++];
        array[2] = this.region.bands[this.curIndex++];
        return true;
    }
}
