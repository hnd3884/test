package com.jhlabs.image;

import java.awt.image.BufferedImage;

public abstract class TransferFilter extends PointFilter
{
    protected int[] rTable;
    protected int[] gTable;
    protected int[] bTable;
    protected boolean initialized;
    
    public TransferFilter() {
        this.initialized = false;
        this.canFilterIndexColorModel = true;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final int a = rgb & 0xFF000000;
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        r = this.rTable[r];
        g = this.gTable[g];
        b = this.bTable[b];
        return a | r << 16 | g << 8 | b;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        if (!this.initialized) {
            this.initialize();
        }
        return super.filter(src, dst);
    }
    
    protected void initialize() {
        this.initialized = true;
        final int[] table = this.makeTable();
        this.bTable = table;
        this.gTable = table;
        this.rTable = table;
    }
    
    protected int[] makeTable() {
        final int[] table = new int[256];
        for (int i = 0; i < 256; ++i) {
            table[i] = PixelUtils.clamp((int)(255.0f * this.transferFunction(i / 255.0f)));
        }
        return table;
    }
    
    protected float transferFunction(final float v) {
        return 0.0f;
    }
    
    public int[] getLUT() {
        if (!this.initialized) {
            this.initialize();
        }
        final int[] lut = new int[256];
        for (int i = 0; i < 256; ++i) {
            lut[i] = this.filterRGB(0, 0, i << 24 | i << 16 | i << 8 | i);
        }
        return lut;
    }
}
