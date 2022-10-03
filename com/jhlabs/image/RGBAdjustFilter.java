package com.jhlabs.image;

public class RGBAdjustFilter extends PointFilter
{
    public float rFactor;
    public float gFactor;
    public float bFactor;
    
    public RGBAdjustFilter() {
        this(0.0f, 0.0f, 0.0f);
    }
    
    public RGBAdjustFilter(final float r, final float g, final float b) {
        this.rFactor = 1.0f + r;
        this.gFactor = 1.0f + g;
        this.bFactor = 1.0f + b;
        this.canFilterIndexColorModel = true;
    }
    
    public void setRFactor(final float rFactor) {
        this.rFactor = 1.0f + rFactor;
    }
    
    public float getRFactor() {
        return this.rFactor - 1.0f;
    }
    
    public void setGFactor(final float gFactor) {
        this.gFactor = 1.0f + gFactor;
    }
    
    public float getGFactor() {
        return this.gFactor - 1.0f;
    }
    
    public void setBFactor(final float bFactor) {
        this.bFactor = 1.0f + bFactor;
    }
    
    public float getBFactor() {
        return this.bFactor - 1.0f;
    }
    
    public int[] getLUT() {
        final int[] lut = new int[256];
        for (int i = 0; i < 256; ++i) {
            lut[i] = this.filterRGB(0, 0, i << 24 | i << 16 | i << 8 | i);
        }
        return lut;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final int a = rgb & 0xFF000000;
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        r = PixelUtils.clamp((int)(r * this.rFactor));
        g = PixelUtils.clamp((int)(g * this.gFactor));
        b = PixelUtils.clamp((int)(b * this.bFactor));
        return a | r << 16 | g << 8 | b;
    }
    
    @Override
    public String toString() {
        return "Colors/Adjust RGB...";
    }
}
