package com.jhlabs.image;

public class JavaLnFFilter extends PointFilter
{
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        if ((x & 0x1) == (y & 0x1)) {
            return rgb;
        }
        return ImageMath.mixColors(0.25f, -6710887, rgb);
    }
    
    @Override
    public String toString() {
        return "Stylize/Java L&F Stipple";
    }
}
