package com.jhlabs.image;

public class UnpremultiplyFilter extends PointFilter
{
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final int a = rgb >> 24 & 0xFF;
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        if (a == 0 || a == 255) {
            return rgb;
        }
        final float f = 255.0f / a;
        r *= (int)f;
        g *= (int)f;
        b *= (int)f;
        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }
        return a << 24 | r << 16 | g << 8 | b;
    }
    
    @Override
    public String toString() {
        return "Alpha/Unpremultiply";
    }
}
