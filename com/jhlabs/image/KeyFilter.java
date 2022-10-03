package com.jhlabs.image;

import java.awt.image.WritableRaster;
import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class KeyFilter extends AbstractBufferedImageOp
{
    private float hTolerance;
    private float sTolerance;
    private float bTolerance;
    private BufferedImage destination;
    private BufferedImage cleanImage;
    
    public KeyFilter() {
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
    
    public void setDestination(final BufferedImage destination) {
        this.destination = destination;
    }
    
    public BufferedImage getDestination() {
        return this.destination;
    }
    
    public void setCleanImage(final BufferedImage cleanImage) {
        this.cleanImage = cleanImage;
    }
    
    public BufferedImage getCleanImage() {
        return this.cleanImage;
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
        if (this.destination != null && this.cleanImage != null) {
            float[] hsb1 = null;
            float[] hsb2 = null;
            int[] inPixels = null;
            int[] outPixels = null;
            int[] cleanPixels = null;
            for (int y = 0; y < height; ++y) {
                inPixels = this.getRGB(src, 0, y, width, 1, inPixels);
                outPixels = this.getRGB(this.destination, 0, y, width, 1, outPixels);
                cleanPixels = this.getRGB(this.cleanImage, 0, y, width, 1, cleanPixels);
                for (int x = 0; x < width; ++x) {
                    final int rgb1 = inPixels[x];
                    final int out = outPixels[x];
                    final int rgb2 = cleanPixels[x];
                    final int r1 = rgb1 >> 16 & 0xFF;
                    final int g1 = rgb1 >> 8 & 0xFF;
                    final int b1 = rgb1 & 0xFF;
                    final int r2 = rgb2 >> 16 & 0xFF;
                    final int g2 = rgb2 >> 8 & 0xFF;
                    final int b2 = rgb2 & 0xFF;
                    hsb1 = Color.RGBtoHSB(r1, b1, g1, hsb1);
                    hsb2 = Color.RGBtoHSB(r2, b2, g2, hsb2);
                    if (Math.abs(hsb1[0] - hsb2[0]) < this.hTolerance && Math.abs(hsb1[1] - hsb2[1]) < this.sTolerance && Math.abs(hsb1[2] - hsb2[2]) < this.bTolerance) {
                        inPixels[x] = out;
                    }
                    else {
                        inPixels[x] = rgb1;
                    }
                }
                this.setRGB(dst, 0, y, width, 1, inPixels);
            }
        }
        return dst;
    }
    
    @Override
    public String toString() {
        return "Keying/Key...";
    }
}
