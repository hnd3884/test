package com.jhlabs.image;

public class SaturationFilter extends PointFilter
{
    public float amount;
    
    public SaturationFilter() {
        this.amount = 1.0f;
    }
    
    public SaturationFilter(final float amount) {
        this.amount = 1.0f;
        this.amount = amount;
        this.canFilterIndexColorModel = true;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        if (this.amount != 1.0f) {
            final int a = rgb & 0xFF000000;
            int r = rgb >> 16 & 0xFF;
            int g = rgb >> 8 & 0xFF;
            int b = rgb & 0xFF;
            final int v = (r + g + b) / 3;
            r = PixelUtils.clamp((int)(v + this.amount * (r - v)));
            g = PixelUtils.clamp((int)(v + this.amount * (g - v)));
            b = PixelUtils.clamp((int)(v + this.amount * (b - v)));
            return a | r << 16 | g << 8 | b;
        }
        return rgb;
    }
    
    @Override
    public String toString() {
        return "Colors/Saturation...";
    }
}
