package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.util.Random;

public class DissolveFilter extends PointFilter
{
    private float density;
    private float softness;
    private float minDensity;
    private float maxDensity;
    private Random randomNumbers;
    
    public DissolveFilter() {
        this.density = 1.0f;
        this.softness = 0.0f;
    }
    
    public void setDensity(final float density) {
        this.density = density;
    }
    
    public float getDensity() {
        return this.density;
    }
    
    public void setSoftness(final float softness) {
        this.softness = softness;
    }
    
    public float getSoftness() {
        return this.softness;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        final float d = (1.0f - this.density) * (1.0f + this.softness);
        this.minDensity = d - this.softness;
        this.maxDensity = d;
        this.randomNumbers = new Random(0L);
        return super.filter(src, dst);
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final int a = rgb >> 24 & 0xFF;
        final float v = this.randomNumbers.nextFloat();
        final float f = ImageMath.smoothStep(this.minDensity, this.maxDensity, v);
        return (int)(a * f) << 24 | (rgb & 0xFFFFFF);
    }
    
    @Override
    public String toString() {
        return "Stylize/Dissolve...";
    }
}
