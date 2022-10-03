package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class DisplaceFilter extends TransformFilter
{
    private float amount;
    private BufferedImage displacementMap;
    private int[] xmap;
    private int[] ymap;
    private int dw;
    private int dh;
    
    public DisplaceFilter() {
        this.amount = 1.0f;
        this.displacementMap = null;
    }
    
    public void setDisplacementMap(final BufferedImage displacementMap) {
        this.displacementMap = displacementMap;
    }
    
    public BufferedImage getDisplacementMap() {
        return this.displacementMap;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        final BufferedImage dm = (this.displacementMap != null) ? this.displacementMap : src;
        this.dw = dm.getWidth();
        this.dh = dm.getHeight();
        int[] mapPixels = new int[this.dw * this.dh];
        this.getRGB(dm, 0, 0, this.dw, this.dh, mapPixels);
        this.xmap = new int[this.dw * this.dh];
        this.ymap = new int[this.dw * this.dh];
        int i = 0;
        for (int y = 0; y < this.dh; ++y) {
            for (int x = 0; x < this.dw; ++x) {
                final int rgb = mapPixels[i];
                final int r = rgb >> 16 & 0xFF;
                final int g = rgb >> 8 & 0xFF;
                final int b = rgb & 0xFF;
                mapPixels[i] = (r + g + b) / 8;
                ++i;
            }
        }
        i = 0;
        for (int y = 0; y < this.dh; ++y) {
            final int j1 = (y + this.dh - 1) % this.dh * this.dw;
            final int j2 = y * this.dw;
            final int j3 = (y + 1) % this.dh * this.dw;
            for (int x2 = 0; x2 < this.dw; ++x2) {
                final int k1 = (x2 + this.dw - 1) % this.dw;
                final int k2 = x2;
                final int k3 = (x2 + 1) % this.dw;
                this.xmap[i] = mapPixels[k1 + j1] + mapPixels[k1 + j2] + mapPixels[k1 + j3] - mapPixels[k3 + j1] - mapPixels[k3 + j2] - mapPixels[k3 + j3];
                this.ymap[i] = mapPixels[k1 + j3] + mapPixels[k2 + j3] + mapPixels[k3 + j3] - mapPixels[k1 + j1] - mapPixels[k2 + j1] - mapPixels[k3 + j1];
                ++i;
            }
        }
        mapPixels = null;
        dst = super.filter(src, dst);
        final int[] array = null;
        this.ymap = array;
        this.xmap = array;
        return dst;
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        final float nx = (float)x;
        final float ny = (float)y;
        final int i = y % this.dh * this.dw + x % this.dw;
        out[0] = x + this.amount * this.xmap[i];
        out[1] = y + this.amount * this.ymap[i];
    }
    
    @Override
    public String toString() {
        return "Distort/Displace...";
    }
}
