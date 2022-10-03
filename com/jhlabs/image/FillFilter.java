package com.jhlabs.image;

public class FillFilter extends PointFilter
{
    private int fillColor;
    
    public FillFilter() {
        this(-16777216);
    }
    
    public FillFilter(final int color) {
        this.fillColor = color;
    }
    
    public void setFillColor(final int fillColor) {
        this.fillColor = fillColor;
    }
    
    public int getFillColor() {
        return this.fillColor;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        return this.fillColor;
    }
}
