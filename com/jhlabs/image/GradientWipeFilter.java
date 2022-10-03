package com.jhlabs.image;

import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class GradientWipeFilter extends AbstractBufferedImageOp
{
    private float density;
    private float softness;
    private boolean invert;
    private BufferedImage mask;
    
    public GradientWipeFilter() {
        this.density = 0.0f;
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
    
    public void setMask(final BufferedImage mask) {
        this.mask = mask;
    }
    
    public BufferedImage getMask() {
        return this.mask;
    }
    
    public void setInvert(final boolean invert) {
        this.invert = invert;
    }
    
    public boolean getInvert() {
        return this.invert;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        if (this.mask == null) {
            return dst;
        }
        final int maskWidth = this.mask.getWidth();
        final int maskHeight = this.mask.getHeight();
        final float d = this.density * (1.0f + this.softness);
        final float lower = 255.0f * (d - this.softness);
        final float upper = 255.0f * d;
        final int[] inPixels = new int[width];
        final int[] maskPixels = new int[maskWidth];
        for (int y = 0; y < height; ++y) {
            this.getRGB(src, 0, y, width, 1, inPixels);
            this.getRGB(this.mask, 0, y % maskHeight, maskWidth, 1, maskPixels);
            for (int x = 0; x < width; ++x) {
                final int maskRGB = maskPixels[x % maskWidth];
                final int inRGB = inPixels[x];
                final int v = PixelUtils.brightness(maskRGB);
                final float f = ImageMath.smoothStep(lower, upper, (float)v);
                int a = (int)(255.0f * f);
                if (this.invert) {
                    a = 255 - a;
                }
                inPixels[x] = (a << 24 | (inRGB & 0xFFFFFF));
            }
            this.setRGB(dst, 0, y, width, 1, inPixels);
        }
        return dst;
    }
    
    @Override
    public String toString() {
        return "Transitions/Gradient Wipe...";
    }
}
