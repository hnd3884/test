package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class StampFilter extends PointFilter
{
    private float threshold;
    private float softness;
    private float radius;
    private float lowerThreshold3;
    private float upperThreshold3;
    private int white;
    private int black;
    
    public StampFilter() {
        this(0.5f);
    }
    
    public StampFilter(final float threshold) {
        this.softness = 0.0f;
        this.radius = 5.0f;
        this.white = -1;
        this.black = -16777216;
        this.setThreshold(threshold);
    }
    
    public void setRadius(final float radius) {
        this.radius = radius;
    }
    
    public float getRadius() {
        return this.radius;
    }
    
    public void setThreshold(final float threshold) {
        this.threshold = threshold;
    }
    
    public float getThreshold() {
        return this.threshold;
    }
    
    public void setSoftness(final float softness) {
        this.softness = softness;
    }
    
    public float getSoftness() {
        return this.softness;
    }
    
    public void setWhite(final int white) {
        this.white = white;
    }
    
    public int getWhite() {
        return this.white;
    }
    
    public void setBlack(final int black) {
        this.black = black;
    }
    
    public int getBlack() {
        return this.black;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        dst = new GaussianFilter((float)(int)this.radius).filter(src, null);
        this.lowerThreshold3 = 765.0f * (this.threshold - this.softness * 0.5f);
        this.upperThreshold3 = 765.0f * (this.threshold + this.softness * 0.5f);
        return super.filter(dst, dst);
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final int a = rgb & 0xFF000000;
        final int r = rgb >> 16 & 0xFF;
        final int g = rgb >> 8 & 0xFF;
        final int b = rgb & 0xFF;
        final int l = r + g + b;
        final float f = ImageMath.smoothStep(this.lowerThreshold3, this.upperThreshold3, (float)l);
        return ImageMath.mixColors(f, this.black, this.white);
    }
    
    @Override
    public String toString() {
        return "Stylize/Stamp...";
    }
}
