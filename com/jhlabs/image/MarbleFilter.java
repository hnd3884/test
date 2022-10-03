package com.jhlabs.image;

import java.awt.image.BufferedImage;
import com.jhlabs.math.Noise;

public class MarbleFilter extends TransformFilter
{
    private float[] sinTable;
    private float[] cosTable;
    private float xScale;
    private float yScale;
    private float amount;
    private float turbulence;
    
    public MarbleFilter() {
        this.xScale = 4.0f;
        this.yScale = 4.0f;
        this.amount = 1.0f;
        this.turbulence = 1.0f;
        this.setEdgeAction(1);
    }
    
    public void setXScale(final float xScale) {
        this.xScale = xScale;
    }
    
    public float getXScale() {
        return this.xScale;
    }
    
    public void setYScale(final float yScale) {
        this.yScale = yScale;
    }
    
    public float getYScale() {
        return this.yScale;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    public void setTurbulence(final float turbulence) {
        this.turbulence = turbulence;
    }
    
    public float getTurbulence() {
        return this.turbulence;
    }
    
    private void initialize() {
        this.sinTable = new float[256];
        this.cosTable = new float[256];
        for (int i = 0; i < 256; ++i) {
            final float angle = 6.2831855f * i / 256.0f * this.turbulence;
            this.sinTable[i] = (float)(-this.yScale * Math.sin(angle));
            this.cosTable[i] = (float)(this.yScale * Math.cos(angle));
        }
    }
    
    private int displacementMap(final int x, final int y) {
        return PixelUtils.clamp((int)(127.0f * (1.0f + Noise.noise2(x / this.xScale, y / this.xScale))));
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        final int displacement = this.displacementMap(x, y);
        out[0] = x + this.sinTable[displacement];
        out[1] = y + this.cosTable[displacement];
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        this.initialize();
        return super.filter(src, dst);
    }
    
    @Override
    public String toString() {
        return "Distort/Marble...";
    }
}
