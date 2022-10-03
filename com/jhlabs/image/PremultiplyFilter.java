package com.jhlabs.image;

public class PremultiplyFilter extends PointFilter
{
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final int a = rgb >> 24 & 0xFF;
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        final float f = a * 0.003921569f;
        r *= (int)f;
        g *= (int)f;
        b *= (int)f;
        return a << 24 | r << 16 | g << 8 | b;
    }
    
    @Override
    public String toString() {
        return "Alpha/Premultiply";
    }
}
