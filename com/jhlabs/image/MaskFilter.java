package com.jhlabs.image;

public class MaskFilter extends PointFilter
{
    private int mask;
    
    public MaskFilter() {
        this(-16711681);
    }
    
    public MaskFilter(final int mask) {
        this.canFilterIndexColorModel = true;
        this.setMask(mask);
    }
    
    public void setMask(final int mask) {
        this.mask = mask;
    }
    
    public int getMask() {
        return this.mask;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        return rgb & this.mask;
    }
    
    @Override
    public String toString() {
        return "Mask";
    }
}
