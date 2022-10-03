package com.jhlabs.image;

import java.awt.Rectangle;

public class LevelsFilter extends WholeImageFilter
{
    private int[][] lut;
    private float lowLevel;
    private float highLevel;
    private float lowOutputLevel;
    private float highOutputLevel;
    
    public LevelsFilter() {
        this.lowLevel = 0.0f;
        this.highLevel = 1.0f;
        this.lowOutputLevel = 0.0f;
        this.highOutputLevel = 1.0f;
    }
    
    public void setLowLevel(final float lowLevel) {
        this.lowLevel = lowLevel;
    }
    
    public float getLowLevel() {
        return this.lowLevel;
    }
    
    public void setHighLevel(final float highLevel) {
        this.highLevel = highLevel;
    }
    
    public float getHighLevel() {
        return this.highLevel;
    }
    
    public void setLowOutputLevel(final float lowOutputLevel) {
        this.lowOutputLevel = lowOutputLevel;
    }
    
    public float getLowOutputLevel() {
        return this.lowOutputLevel;
    }
    
    public void setHighOutputLevel(final float highOutputLevel) {
        this.highOutputLevel = highOutputLevel;
    }
    
    public float getHighOutputLevel() {
        return this.highOutputLevel;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final Histogram histogram = new Histogram(inPixels, width, height, 0, width);
        if (histogram.getNumSamples() > 0) {
            final float scale = 255.0f / histogram.getNumSamples();
            this.lut = new int[3][256];
            final float low = this.lowLevel * 255.0f;
            float high = this.highLevel * 255.0f;
            if (low == high) {
                ++high;
            }
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 256; ++j) {
                    this.lut[i][j] = PixelUtils.clamp((int)(255.0f * (this.lowOutputLevel + (this.highOutputLevel - this.lowOutputLevel) * (j - low) / (high - low))));
                }
            }
        }
        else {
            this.lut = null;
        }
        int i = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                inPixels[i] = this.filterRGB(x, y, inPixels[i]);
                ++i;
            }
        }
        this.lut = null;
        return inPixels;
    }
    
    public int filterRGB(final int x, final int y, final int rgb) {
        if (this.lut != null) {
            final int a = rgb & 0xFF000000;
            final int r = this.lut[0][rgb >> 16 & 0xFF];
            final int g = this.lut[1][rgb >> 8 & 0xFF];
            final int b = this.lut[2][rgb & 0xFF];
            return a | r << 16 | g << 8 | b;
        }
        return rgb;
    }
    
    @Override
    public String toString() {
        return "Colors/Levels...";
    }
}
