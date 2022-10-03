package com.jhlabs.image;

import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class GlintFilter extends AbstractBufferedImageOp
{
    private float threshold;
    private int length;
    private float blur;
    private float amount;
    private boolean glintOnly;
    private Colormap colormap;
    
    public GlintFilter() {
        this.threshold = 1.0f;
        this.length = 5;
        this.blur = 0.0f;
        this.amount = 0.1f;
        this.glintOnly = false;
        this.colormap = new LinearColormap(-1, -16777216);
    }
    
    public void setThreshold(final float threshold) {
        this.threshold = threshold;
    }
    
    public float getThreshold() {
        return this.threshold;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    public void setLength(final int length) {
        this.length = length;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public void setBlur(final float blur) {
        this.blur = blur;
    }
    
    public float getBlur() {
        return this.blur;
    }
    
    public void setGlintOnly(final boolean glintOnly) {
        this.glintOnly = glintOnly;
    }
    
    public boolean getGlintOnly() {
        return this.glintOnly;
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final int[] pixels = new int[width];
        final int length2 = (int)(this.length / 1.414f);
        final int[] colors = new int[this.length + 1];
        final int[] colors2 = new int[length2 + 1];
        if (this.colormap != null) {
            for (int i = 0; i <= this.length; ++i) {
                int argb = this.colormap.getColor(i / (float)this.length);
                final int r = argb >> 16 & 0xFF;
                final int g = argb >> 8 & 0xFF;
                final int b = argb & 0xFF;
                argb = ((argb & 0xFF000000) | (int)(this.amount * r) << 16 | (int)(this.amount * g) << 8 | (int)(this.amount * b));
                colors[i] = argb;
            }
            for (int i = 0; i <= length2; ++i) {
                int argb = this.colormap.getColor(i / (float)length2);
                final int r = argb >> 16 & 0xFF;
                final int g = argb >> 8 & 0xFF;
                final int b = argb & 0xFF;
                argb = ((argb & 0xFF000000) | (int)(this.amount * r) << 16 | (int)(this.amount * g) << 8 | (int)(this.amount * b));
                colors2[i] = argb;
            }
        }
        BufferedImage mask = new BufferedImage(width, height, 2);
        final int threshold3 = (int)(this.threshold * 3.0f * 255.0f);
        for (int y = 0; y < height; ++y) {
            this.getRGB(src, 0, y, width, 1, pixels);
            for (int x = 0; x < width; ++x) {
                final int rgb = pixels[x];
                final int a = rgb & 0xFF000000;
                final int r2 = rgb >> 16 & 0xFF;
                final int g2 = rgb >> 8 & 0xFF;
                final int b2 = rgb & 0xFF;
                int l = r2 + g2 + b2;
                if (l < threshold3) {
                    pixels[x] = -16777216;
                }
                else {
                    l /= 3;
                    pixels[x] = (a | l << 16 | l << 8 | l);
                }
            }
            this.setRGB(mask, 0, y, width, 1, pixels);
        }
        if (this.blur != 0.0f) {
            mask = new GaussianFilter(this.blur).filter(mask, null);
        }
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        int[] dstPixels;
        if (this.glintOnly) {
            dstPixels = new int[width * height];
        }
        else {
            dstPixels = this.getRGB(src, 0, 0, width, height, null);
        }
        for (int y2 = 0; y2 < height; ++y2) {
            int index = y2 * width;
            this.getRGB(mask, 0, y2, width, 1, pixels);
            final int ymin = Math.max(y2 - this.length, 0) - y2;
            final int ymax = Math.min(y2 + this.length, height - 1) - y2;
            final int ymin2 = Math.max(y2 - length2, 0) - y2;
            final int ymax2 = Math.min(y2 + length2, height - 1) - y2;
            for (int x2 = 0; x2 < width; ++x2) {
                if ((pixels[x2] & 0xFF) > this.threshold * 255.0f) {
                    final int xmin = Math.max(x2 - this.length, 0) - x2;
                    final int xmax = Math.min(x2 + this.length, width - 1) - x2;
                    final int xmin2 = Math.max(x2 - length2, 0) - x2;
                    final int xmax2 = Math.min(x2 + length2, width - 1) - x2;
                    for (int j = 0, k = 0; j <= xmax; ++j, ++k) {
                        dstPixels[index + j] = PixelUtils.combinePixels(dstPixels[index + j], colors[k], 4);
                    }
                    for (int j = -1, k = 1; j >= xmin; --j, ++k) {
                        dstPixels[index + j] = PixelUtils.combinePixels(dstPixels[index + j], colors[k], 4);
                    }
                    for (int j = 1, m = index + width, k2 = 0; j <= ymax; ++j, m += width, ++k2) {
                        dstPixels[m] = PixelUtils.combinePixels(dstPixels[m], colors[k2], 4);
                    }
                    for (int j = -1, m = index - width, k2 = 0; j >= ymin; --j, m -= width, ++k2) {
                        dstPixels[m] = PixelUtils.combinePixels(dstPixels[m], colors[k2], 4);
                    }
                    final int xymin = Math.max(xmin2, ymin2);
                    final int xymax = Math.min(xmax2, ymax2);
                    for (int count = Math.min(xmax2, ymax2), i2 = 1, j2 = index + width + 1, k3 = 0; i2 <= count; ++i2, j2 += width + 1, ++k3) {
                        dstPixels[j2] = PixelUtils.combinePixels(dstPixels[j2], colors2[k3], 4);
                    }
                    for (int count = Math.min(-xmin2, -ymin2), i2 = 1, j2 = index - width - 1, k3 = 0; i2 <= count; ++i2, j2 -= width + 1, ++k3) {
                        dstPixels[j2] = PixelUtils.combinePixels(dstPixels[j2], colors2[k3], 4);
                    }
                    for (int count = Math.min(xmax2, -ymin2), i2 = 1, j2 = index - width + 1, k3 = 0; i2 <= count; ++i2, j2 += -width + 1, ++k3) {
                        dstPixels[j2] = PixelUtils.combinePixels(dstPixels[j2], colors2[k3], 4);
                    }
                    for (int count = Math.min(-xmin2, ymax2), i2 = 1, j2 = index + width - 1, k3 = 0; i2 <= count; ++i2, j2 += width - 1, ++k3) {
                        dstPixels[j2] = PixelUtils.combinePixels(dstPixels[j2], colors2[k3], 4);
                    }
                }
                ++index;
            }
        }
        this.setRGB(dst, 0, 0, width, height, dstPixels);
        return dst;
    }
    
    @Override
    public String toString() {
        return "Effects/Glint...";
    }
}
