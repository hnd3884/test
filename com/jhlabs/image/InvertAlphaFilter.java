package com.jhlabs.image;

public class InvertAlphaFilter extends PointFilter
{
    public InvertAlphaFilter() {
        this.canFilterIndexColorModel = true;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        return rgb ^ 0xFF000000;
    }
    
    @Override
    public String toString() {
        return "Alpha/Invert";
    }
}
