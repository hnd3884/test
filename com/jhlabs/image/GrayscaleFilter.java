package com.jhlabs.image;

public class GrayscaleFilter extends PointFilter
{
    public GrayscaleFilter() {
        this.canFilterIndexColorModel = true;
    }
    
    @Override
    public int filterRGB(final int x, final int y, int rgb) {
        final int a = rgb & 0xFF000000;
        final int r = rgb >> 16 & 0xFF;
        final int g = rgb >> 8 & 0xFF;
        final int b = rgb & 0xFF;
        rgb = r * 77 + g * 151 + b * 28 >> 8;
        return a | rgb << 16 | rgb << 8 | rgb;
    }
    
    @Override
    public String toString() {
        return "Colors/Grayscale";
    }
}
