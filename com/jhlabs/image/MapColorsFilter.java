package com.jhlabs.image;

public class MapColorsFilter extends PointFilter
{
    private int oldColor;
    private int newColor;
    
    public MapColorsFilter() {
        this(-1, -16777216);
    }
    
    public MapColorsFilter(final int oldColor, final int newColor) {
        this.canFilterIndexColorModel = true;
        this.oldColor = oldColor;
        this.newColor = newColor;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        if (rgb == this.oldColor) {
            return this.newColor;
        }
        return rgb;
    }
}
