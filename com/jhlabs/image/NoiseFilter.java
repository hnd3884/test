package com.jhlabs.image;

import java.util.Random;

public class NoiseFilter extends PointFilter
{
    public static final int GAUSSIAN = 0;
    public static final int UNIFORM = 1;
    private int amount;
    private int distribution;
    private boolean monochrome;
    private float density;
    private Random randomNumbers;
    
    public NoiseFilter() {
        this.amount = 25;
        this.distribution = 1;
        this.monochrome = false;
        this.density = 1.0f;
        this.randomNumbers = new Random();
    }
    
    public void setAmount(final int amount) {
        this.amount = amount;
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public void setDistribution(final int distribution) {
        this.distribution = distribution;
    }
    
    public int getDistribution() {
        return this.distribution;
    }
    
    public void setMonochrome(final boolean monochrome) {
        this.monochrome = monochrome;
    }
    
    public boolean getMonochrome() {
        return this.monochrome;
    }
    
    public void setDensity(final float density) {
        this.density = density;
    }
    
    public float getDensity() {
        return this.density;
    }
    
    private int random(int x) {
        x += (int)(((this.distribution == 0) ? this.randomNumbers.nextGaussian() : (2.0f * this.randomNumbers.nextFloat() - 1.0f)) * this.amount);
        if (x < 0) {
            x = 0;
        }
        else if (x > 255) {
            x = 255;
        }
        return x;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        if (this.randomNumbers.nextFloat() <= this.density) {
            final int a = rgb & 0xFF000000;
            int r = rgb >> 16 & 0xFF;
            int g = rgb >> 8 & 0xFF;
            int b = rgb & 0xFF;
            if (this.monochrome) {
                final int n = (int)(((this.distribution == 0) ? this.randomNumbers.nextGaussian() : (2.0f * this.randomNumbers.nextFloat() - 1.0f)) * this.amount);
                r = PixelUtils.clamp(r + n);
                g = PixelUtils.clamp(g + n);
                b = PixelUtils.clamp(b + n);
            }
            else {
                r = this.random(r);
                g = this.random(g);
                b = this.random(b);
            }
            return a | r << 16 | g << 8 | b;
        }
        return rgb;
    }
    
    @Override
    public String toString() {
        return "Stylize/Add Noise...";
    }
}
