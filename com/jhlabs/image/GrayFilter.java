package com.jhlabs.image;

public class GrayFilter extends PointFilter
{
    public GrayFilter() {
        this.canFilterIndexColorModel = true;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final int a = rgb & 0xFF000000;
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        r = (r + 255) / 2;
        g = (g + 255) / 2;
        b = (b + 255) / 2;
        return a | r << 16 | g << 8 | b;
    }
    
    @Override
    public String toString() {
        return "Colors/Gray Out";
    }
}
