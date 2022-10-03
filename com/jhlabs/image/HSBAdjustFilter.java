package com.jhlabs.image;

import java.awt.Color;

public class HSBAdjustFilter extends PointFilter
{
    public float hFactor;
    public float sFactor;
    public float bFactor;
    private float[] hsb;
    
    public HSBAdjustFilter() {
        this(0.0f, 0.0f, 0.0f);
    }
    
    public HSBAdjustFilter(final float r, final float g, final float b) {
        this.hsb = new float[3];
        this.hFactor = r;
        this.sFactor = g;
        this.bFactor = b;
        this.canFilterIndexColorModel = true;
    }
    
    public void setHFactor(final float hFactor) {
        this.hFactor = hFactor;
    }
    
    public float getHFactor() {
        return this.hFactor;
    }
    
    public void setSFactor(final float sFactor) {
        this.sFactor = sFactor;
    }
    
    public float getSFactor() {
        return this.sFactor;
    }
    
    public void setBFactor(final float bFactor) {
        this.bFactor = bFactor;
    }
    
    public float getBFactor() {
        return this.bFactor;
    }
    
    @Override
    public int filterRGB(final int x, final int y, int rgb) {
        final int a = rgb & 0xFF000000;
        final int r = rgb >> 16 & 0xFF;
        final int g = rgb >> 8 & 0xFF;
        final int b = rgb & 0xFF;
        Color.RGBtoHSB(r, g, b, this.hsb);
        final float[] hsb = this.hsb;
        final int n = 0;
        hsb[n] += this.hFactor;
        while (this.hsb[0] < 0.0f) {
            final float[] hsb2 = this.hsb;
            final int n2 = 0;
            hsb2[n2] += (float)6.283185307179586;
        }
        final float[] hsb3 = this.hsb;
        final int n3 = 1;
        hsb3[n3] += this.sFactor;
        if (this.hsb[1] < 0.0f) {
            this.hsb[1] = 0.0f;
        }
        else if (this.hsb[1] > 1.0) {
            this.hsb[1] = 1.0f;
        }
        final float[] hsb4 = this.hsb;
        final int n4 = 2;
        hsb4[n4] += this.bFactor;
        if (this.hsb[2] < 0.0f) {
            this.hsb[2] = 0.0f;
        }
        else if (this.hsb[2] > 1.0) {
            this.hsb[2] = 1.0f;
        }
        rgb = Color.HSBtoRGB(this.hsb[0], this.hsb[1], this.hsb[2]);
        return a | (rgb & 0xFFFFFF);
    }
    
    @Override
    public String toString() {
        return "Colors/Adjust HSB...";
    }
}
