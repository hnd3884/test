package com.jhlabs.image;

import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class UnsharpFilter extends GaussianFilter
{
    private float amount;
    private int threshold;
    
    public UnsharpFilter() {
        this.amount = 0.5f;
        this.threshold = 1;
        this.radius = 2.0f;
    }
    
    public void setThreshold(final int threshold) {
        this.threshold = threshold;
    }
    
    public int getThreshold() {
        return this.threshold;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final int[] inPixels = new int[width * height];
        final int[] outPixels = new int[width * height];
        src.getRGB(0, 0, width, height, inPixels, 0, width);
        if (this.radius > 0.0f) {
            GaussianFilter.convolveAndTranspose(this.kernel, inPixels, outPixels, width, height, this.alpha, this.alpha && this.premultiplyAlpha, false, UnsharpFilter.CLAMP_EDGES);
            GaussianFilter.convolveAndTranspose(this.kernel, outPixels, inPixels, height, width, this.alpha, false, this.alpha && this.premultiplyAlpha, UnsharpFilter.CLAMP_EDGES);
        }
        src.getRGB(0, 0, width, height, outPixels, 0, width);
        final float a = 4.0f * this.amount;
        int index = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int rgb1 = outPixels[index];
                int r1 = rgb1 >> 16 & 0xFF;
                int g1 = rgb1 >> 8 & 0xFF;
                int b1 = rgb1 & 0xFF;
                final int rgb2 = inPixels[index];
                final int r2 = rgb2 >> 16 & 0xFF;
                final int g2 = rgb2 >> 8 & 0xFF;
                final int b2 = rgb2 & 0xFF;
                if (Math.abs(r1 - r2) >= this.threshold) {
                    r1 = PixelUtils.clamp((int)((a + 1.0f) * (r1 - r2) + r2));
                }
                if (Math.abs(g1 - g2) >= this.threshold) {
                    g1 = PixelUtils.clamp((int)((a + 1.0f) * (g1 - g2) + g2));
                }
                if (Math.abs(b1 - b2) >= this.threshold) {
                    b1 = PixelUtils.clamp((int)((a + 1.0f) * (b1 - b2) + b2));
                }
                inPixels[index] = ((rgb1 & 0xFF000000) | r1 << 16 | g1 << 8 | b1);
                ++index;
            }
        }
        dst.setRGB(0, 0, width, height, inPixels, 0, width);
        return dst;
    }
    
    @Override
    public String toString() {
        return "Blur/Unsharp Mask...";
    }
}
