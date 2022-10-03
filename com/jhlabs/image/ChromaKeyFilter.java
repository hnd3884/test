package com.jhlabs.image;

import java.awt.image.WritableRaster;
import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class ChromaKeyFilter extends AbstractBufferedImageOp
{
    private float hTolerance;
    private float sTolerance;
    private float bTolerance;
    private int color;
    
    public ChromaKeyFilter() {
        this.hTolerance = 0.0f;
        this.sTolerance = 0.0f;
        this.bTolerance = 0.0f;
    }
    
    public void setHTolerance(final float hTolerance) {
        this.hTolerance = hTolerance;
    }
    
    public float getHTolerance() {
        return this.hTolerance;
    }
    
    public void setSTolerance(final float sTolerance) {
        this.sTolerance = sTolerance;
    }
    
    public float getSTolerance() {
        return this.sTolerance;
    }
    
    public void setBTolerance(final float bTolerance) {
        this.bTolerance = bTolerance;
    }
    
    public float getBTolerance() {
        return this.bTolerance;
    }
    
    public void setColor(final int color) {
        this.color = color;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final int type = src.getType();
        final WritableRaster srcRaster = src.getRaster();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final WritableRaster dstRaster = dst.getRaster();
        float[] hsb1 = null;
        float[] hsb2 = null;
        final int rgb2 = this.color;
        final int r2 = rgb2 >> 16 & 0xFF;
        final int g2 = rgb2 >> 8 & 0xFF;
        final int b2 = rgb2 & 0xFF;
        hsb2 = Color.RGBtoHSB(r2, b2, g2, hsb2);
        int[] inPixels = null;
        for (int y = 0; y < height; ++y) {
            inPixels = this.getRGB(src, 0, y, width, 1, inPixels);
            for (int x = 0; x < width; ++x) {
                final int rgb3 = inPixels[x];
                final int r3 = rgb3 >> 16 & 0xFF;
                final int g3 = rgb3 >> 8 & 0xFF;
                final int b3 = rgb3 & 0xFF;
                hsb1 = Color.RGBtoHSB(r3, b3, g3, hsb1);
                if (Math.abs(hsb1[0] - hsb2[0]) < this.hTolerance && Math.abs(hsb1[1] - hsb2[1]) < this.sTolerance && Math.abs(hsb1[2] - hsb2[2]) < this.bTolerance) {
                    inPixels[x] = (rgb3 & 0xFFFFFF);
                }
                else {
                    inPixels[x] = rgb3;
                }
            }
            this.setRGB(dst, 0, y, width, 1, inPixels);
        }
        return dst;
    }
    
    @Override
    public String toString() {
        return "Keying/Chroma Key...";
    }
}
