package com.jhlabs.image;

public class InvertFilter extends PointFilter
{
    public InvertFilter() {
        this.canFilterIndexColorModel = true;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final int a = rgb & 0xFF000000;
        return a | (~rgb & 0xFFFFFF);
    }
    
    @Override
    public String toString() {
        return "Colors/Invert";
    }
}
