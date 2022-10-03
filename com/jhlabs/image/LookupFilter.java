package com.jhlabs.image;

public class LookupFilter extends PointFilter
{
    private Colormap colormap;
    
    public LookupFilter() {
        this.colormap = new Gradient();
        this.canFilterIndexColorModel = true;
    }
    
    public LookupFilter(final Colormap colormap) {
        this.colormap = new Gradient();
        this.canFilterIndexColorModel = true;
        this.colormap = colormap;
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    @Override
    public int filterRGB(final int x, final int y, int rgb) {
        final int r = rgb >> 16 & 0xFF;
        final int g = rgb >> 8 & 0xFF;
        final int b = rgb & 0xFF;
        rgb = (r + g + b) / 3;
        return this.colormap.getColor(rgb / 255.0f);
    }
    
    @Override
    public String toString() {
        return "Colors/Lookup...";
    }
}
