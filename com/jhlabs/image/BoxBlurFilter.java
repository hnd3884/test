package com.jhlabs.image;

import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class BoxBlurFilter extends AbstractBufferedImageOp
{
    private float hRadius;
    private float vRadius;
    private int iterations;
    private boolean premultiplyAlpha;
    
    public BoxBlurFilter() {
        this.iterations = 1;
        this.premultiplyAlpha = true;
    }
    
    public BoxBlurFilter(final float hRadius, final float vRadius, final int iterations) {
        this.iterations = 1;
        this.premultiplyAlpha = true;
        this.hRadius = hRadius;
        this.vRadius = vRadius;
        this.iterations = iterations;
    }
    
    public void setPremultiplyAlpha(final boolean premultiplyAlpha) {
        this.premultiplyAlpha = premultiplyAlpha;
    }
    
    public boolean getPremultiplyAlpha() {
        return this.premultiplyAlpha;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final int[] inPixels = new int[width * height];
        final int[] outPixels = new int[width * height];
        this.getRGB(src, 0, 0, width, height, inPixels);
        if (this.premultiplyAlpha) {
            ImageMath.premultiply(inPixels, 0, inPixels.length);
        }
        for (int i = 0; i < this.iterations; ++i) {
            blur(inPixels, outPixels, width, height, this.hRadius);
            blur(outPixels, inPixels, height, width, this.vRadius);
        }
        blurFractional(inPixels, outPixels, width, height, this.hRadius);
        blurFractional(outPixels, inPixels, height, width, this.vRadius);
        if (this.premultiplyAlpha) {
            ImageMath.unpremultiply(inPixels, 0, inPixels.length);
        }
        this.setRGB(dst, 0, 0, width, height, inPixels);
        return dst;
    }
    
    public static void blur(final int[] in, final int[] out, final int width, final int height, final float radius) {
        final int widthMinus1 = width - 1;
        final int r = (int)radius;
        final int tableSize = 2 * r + 1;
        final int[] divide = new int[256 * tableSize];
        for (int i = 0; i < 256 * tableSize; ++i) {
            divide[i] = i / tableSize;
        }
        int inIndex = 0;
        for (int y = 0; y < height; ++y) {
            int outIndex = y;
            int ta = 0;
            int tr = 0;
            int tg = 0;
            int tb = 0;
            for (int j = -r; j <= r; ++j) {
                final int rgb = in[inIndex + ImageMath.clamp(j, 0, width - 1)];
                ta += (rgb >> 24 & 0xFF);
                tr += (rgb >> 16 & 0xFF);
                tg += (rgb >> 8 & 0xFF);
                tb += (rgb & 0xFF);
            }
            for (int x = 0; x < width; ++x) {
                out[outIndex] = (divide[ta] << 24 | divide[tr] << 16 | divide[tg] << 8 | divide[tb]);
                int i2 = x + r + 1;
                if (i2 > widthMinus1) {
                    i2 = widthMinus1;
                }
                int i3 = x - r;
                if (i3 < 0) {
                    i3 = 0;
                }
                final int rgb2 = in[inIndex + i2];
                final int rgb3 = in[inIndex + i3];
                ta += (rgb2 >> 24 & 0xFF) - (rgb3 >> 24 & 0xFF);
                tr += (rgb2 & 0xFF0000) - (rgb3 & 0xFF0000) >> 16;
                tg += (rgb2 & 0xFF00) - (rgb3 & 0xFF00) >> 8;
                tb += (rgb2 & 0xFF) - (rgb3 & 0xFF);
                outIndex += height;
            }
            inIndex += width;
        }
    }
    
    public static void blurFractional(final int[] in, final int[] out, final int width, final int height, float radius) {
        radius -= (int)radius;
        final float f = 1.0f / (1.0f + 2.0f * radius);
        int inIndex = 0;
        for (int y = 0; y < height; ++y) {
            int outIndex = y;
            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; ++x) {
                final int i = inIndex + x;
                final int rgb1 = in[i - 1];
                final int rgb2 = in[i];
                final int rgb3 = in[i + 1];
                int a1 = rgb1 >> 24 & 0xFF;
                int r1 = rgb1 >> 16 & 0xFF;
                int g1 = rgb1 >> 8 & 0xFF;
                int b1 = rgb1 & 0xFF;
                final int a2 = rgb2 >> 24 & 0xFF;
                final int r2 = rgb2 >> 16 & 0xFF;
                final int g2 = rgb2 >> 8 & 0xFF;
                final int b2 = rgb2 & 0xFF;
                final int a3 = rgb3 >> 24 & 0xFF;
                final int r3 = rgb3 >> 16 & 0xFF;
                final int g3 = rgb3 >> 8 & 0xFF;
                final int b3 = rgb3 & 0xFF;
                a1 = a2 + (int)((a1 + a3) * radius);
                r1 = r2 + (int)((r1 + r3) * radius);
                g1 = g2 + (int)((g1 + g3) * radius);
                b1 = b2 + (int)((b1 + b3) * radius);
                a1 *= (int)f;
                r1 *= (int)f;
                g1 *= (int)f;
                b1 *= (int)f;
                out[outIndex] = (a1 << 24 | r1 << 16 | g1 << 8 | b1);
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }
    
    public void setHRadius(final float hRadius) {
        this.hRadius = hRadius;
    }
    
    public float getHRadius() {
        return this.hRadius;
    }
    
    public void setVRadius(final float vRadius) {
        this.vRadius = vRadius;
    }
    
    public float getVRadius() {
        return this.vRadius;
    }
    
    public void setRadius(final float radius) {
        this.vRadius = radius;
        this.hRadius = radius;
    }
    
    public float getRadius() {
        return this.hRadius;
    }
    
    public void setIterations(final int iterations) {
        this.iterations = iterations;
    }
    
    public int getIterations() {
        return this.iterations;
    }
    
    @Override
    public String toString() {
        return "Blur/Box Blur...";
    }
}
