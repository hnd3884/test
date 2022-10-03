package com.jhlabs.image;

import java.awt.image.Kernel;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class SmartBlurFilter extends AbstractBufferedImageOp
{
    private int hRadius;
    private int vRadius;
    private int threshold;
    
    public SmartBlurFilter() {
        this.hRadius = 5;
        this.vRadius = 5;
        this.threshold = 10;
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
        final Kernel kernel = GaussianFilter.makeKernel((float)this.hRadius);
        this.thresholdBlur(kernel, inPixels, outPixels, width, height, true);
        this.thresholdBlur(kernel, outPixels, inPixels, height, width, true);
        this.setRGB(dst, 0, 0, width, height, inPixels);
        return dst;
    }
    
    private void thresholdBlur(final Kernel kernel, final int[] inPixels, final int[] outPixels, final int width, final int height, final boolean alpha) {
        final int index = 0;
        final float[] matrix = kernel.getKernelData(null);
        final int cols = kernel.getWidth();
        final int cols2 = cols / 2;
        for (int y = 0; y < height; ++y) {
            final int ioffset = y * width;
            int outIndex = y;
            for (int x = 0; x < width; ++x) {
                float r = 0.0f;
                float g = 0.0f;
                float b = 0.0f;
                float a = 0.0f;
                final int moffset = cols2;
                final int rgb1 = inPixels[ioffset + x];
                final int a2 = rgb1 >> 24 & 0xFF;
                final int r2 = rgb1 >> 16 & 0xFF;
                final int g2 = rgb1 >> 8 & 0xFF;
                final int b2 = rgb1 & 0xFF;
                float af = 0.0f;
                float rf = 0.0f;
                float gf = 0.0f;
                float bf = 0.0f;
                for (int col = -cols2; col <= cols2; ++col) {
                    final float f = matrix[moffset + col];
                    if (f != 0.0f) {
                        int ix = x + col;
                        if (0 > ix || ix >= width) {
                            ix = x;
                        }
                        final int rgb2 = inPixels[ioffset + ix];
                        final int a3 = rgb2 >> 24 & 0xFF;
                        final int r3 = rgb2 >> 16 & 0xFF;
                        final int g3 = rgb2 >> 8 & 0xFF;
                        final int b3 = rgb2 & 0xFF;
                        int d = a2 - a3;
                        if (d >= -this.threshold && d <= this.threshold) {
                            a += f * a3;
                            af += f;
                        }
                        d = r2 - r3;
                        if (d >= -this.threshold && d <= this.threshold) {
                            r += f * r3;
                            rf += f;
                        }
                        d = g2 - g3;
                        if (d >= -this.threshold && d <= this.threshold) {
                            g += f * g3;
                            gf += f;
                        }
                        d = b2 - b3;
                        if (d >= -this.threshold && d <= this.threshold) {
                            b += f * b3;
                            bf += f;
                        }
                    }
                }
                a = ((af == 0.0f) ? ((float)a2) : (a / af));
                r = ((rf == 0.0f) ? ((float)r2) : (r / rf));
                g = ((gf == 0.0f) ? ((float)g2) : (g / gf));
                b = ((bf == 0.0f) ? ((float)b2) : (b / bf));
                final int ia = alpha ? PixelUtils.clamp((int)(a + 0.5)) : 255;
                final int ir = PixelUtils.clamp((int)(r + 0.5));
                final int ig = PixelUtils.clamp((int)(g + 0.5));
                final int ib = PixelUtils.clamp((int)(b + 0.5));
                outPixels[outIndex] = (ia << 24 | ir << 16 | ig << 8 | ib);
                outIndex += height;
            }
        }
    }
    
    public void setHRadius(final int hRadius) {
        this.hRadius = hRadius;
    }
    
    public int getHRadius() {
        return this.hRadius;
    }
    
    public void setVRadius(final int vRadius) {
        this.vRadius = vRadius;
    }
    
    public int getVRadius() {
        return this.vRadius;
    }
    
    public void setRadius(final int radius) {
        this.vRadius = radius;
        this.hRadius = radius;
    }
    
    public int getRadius() {
        return this.hRadius;
    }
    
    public void setThreshold(final int threshold) {
        this.threshold = threshold;
    }
    
    public int getThreshold() {
        return this.threshold;
    }
    
    @Override
    public String toString() {
        return "Blur/Smart Blur...";
    }
}
