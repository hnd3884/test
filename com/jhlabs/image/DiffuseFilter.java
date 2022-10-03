package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class DiffuseFilter extends TransformFilter
{
    private float[] sinTable;
    private float[] cosTable;
    private float scale;
    
    public DiffuseFilter() {
        this.scale = 4.0f;
        this.setEdgeAction(1);
    }
    
    public void setScale(final float scale) {
        this.scale = scale;
    }
    
    public float getScale() {
        return this.scale;
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        final int angle = (int)(Math.random() * 255.0);
        final float distance = (float)Math.random();
        out[0] = x + distance * this.sinTable[angle];
        out[1] = y + distance * this.cosTable[angle];
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        this.sinTable = new float[256];
        this.cosTable = new float[256];
        for (int i = 0; i < 256; ++i) {
            final float angle = 6.2831855f * i / 256.0f;
            this.sinTable[i] = (float)(this.scale * Math.sin(angle));
            this.cosTable[i] = (float)(this.scale * Math.cos(angle));
        }
        return super.filter(src, dst);
    }
    
    @Override
    public String toString() {
        return "Distort/Diffuse...";
    }
}
