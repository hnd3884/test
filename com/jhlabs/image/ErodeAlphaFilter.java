package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class ErodeAlphaFilter extends PointFilter
{
    private float threshold;
    private float softness;
    protected float radius;
    private float lowerThreshold;
    private float upperThreshold;
    
    public ErodeAlphaFilter() {
        this(3.0f, 0.75f, 0.0f);
    }
    
    public ErodeAlphaFilter(final float radius, final float threshold, final float softness) {
        this.softness = 0.0f;
        this.radius = 5.0f;
        this.radius = radius;
        this.threshold = threshold;
        this.softness = softness;
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
    
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        dst = new GaussianFilter((float)(int)this.radius).filter(src, null);
        this.lowerThreshold = 255.0f * (this.threshold - this.softness * 0.5f);
        this.upperThreshold = 255.0f * (this.threshold + this.softness * 0.5f);
        return super.filter(dst, dst);
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        int a = rgb >> 24 & 0xFF;
        final int r = rgb >> 16 & 0xFF;
        final int g = rgb >> 8 & 0xFF;
        final int b = rgb & 0xFF;
        if (a == 255) {
            return -1;
        }
        final float f = ImageMath.smoothStep(this.lowerThreshold, this.upperThreshold, (float)a);
        a = (int)(f * 255.0f);
        if (a < 0) {
            a = 0;
        }
        else if (a > 255) {
            a = 255;
        }
        return a << 24 | 0xFFFFFF;
    }
    
    @Override
    public String toString() {
        return "Alpha/Erode...";
    }
}
