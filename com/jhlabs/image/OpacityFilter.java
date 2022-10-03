package com.jhlabs.image;

public class OpacityFilter extends PointFilter
{
    private int opacity;
    private int opacity24;
    
    public OpacityFilter() {
        this(136);
    }
    
    public OpacityFilter(final int opacity) {
        this.setOpacity(opacity);
    }
    
    public void setOpacity(final int opacity) {
        this.opacity = opacity;
        this.opacity24 = opacity << 24;
    }
    
    public int getOpacity() {
        return this.opacity;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        if ((rgb & 0xFF000000) != 0x0) {
            return (rgb & 0xFFFFFF) | this.opacity24;
        }
        return rgb;
    }
    
    @Override
    public String toString() {
        return "Colors/Transparency...";
    }
}
