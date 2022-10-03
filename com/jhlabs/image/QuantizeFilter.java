package com.jhlabs.image;

import java.awt.Rectangle;

public class QuantizeFilter extends WholeImageFilter
{
    protected static final int[] matrix;
    private int sum;
    private boolean dither;
    private int numColors;
    private boolean serpentine;
    
    public QuantizeFilter() {
        this.sum = 16;
        this.numColors = 256;
        this.serpentine = true;
    }
    
    public void setNumColors(final int numColors) {
        this.numColors = Math.min(Math.max(numColors, 8), 256);
    }
    
    public int getNumColors() {
        return this.numColors;
    }
    
    public void setDither(final boolean dither) {
        this.dither = dither;
    }
    
    public boolean getDither() {
        return this.dither;
    }
    
    public void setSerpentine(final boolean serpentine) {
        this.serpentine = serpentine;
    }
    
    public boolean getSerpentine() {
        return this.serpentine;
    }
    
    public void quantize(final int[] inPixels, final int[] outPixels, final int width, final int height, final int numColors, final boolean dither, final boolean serpentine) {
        final int count = width * height;
        final Quantizer quantizer = new OctTreeQuantizer();
        quantizer.setup(numColors);
        quantizer.addPixels(inPixels, 0, count);
        final int[] table = quantizer.buildColorTable();
        if (!dither) {
            for (int i = 0; i < count; ++i) {
                outPixels[i] = table[quantizer.getIndexForColor(inPixels[i])];
            }
        }
        else {
            int index = 0;
            for (int y = 0; y < height; ++y) {
                final boolean reverse = serpentine && (y & 0x1) == 0x1;
                int direction;
                if (reverse) {
                    index = y * width + width - 1;
                    direction = -1;
                }
                else {
                    index = y * width;
                    direction = 1;
                }
                for (int x = 0; x < width; ++x) {
                    int rgb1 = inPixels[index];
                    final int rgb2 = table[quantizer.getIndexForColor(rgb1)];
                    outPixels[index] = rgb2;
                    int r1 = rgb1 >> 16 & 0xFF;
                    int g1 = rgb1 >> 8 & 0xFF;
                    int b1 = rgb1 & 0xFF;
                    final int r2 = rgb2 >> 16 & 0xFF;
                    final int g2 = rgb2 >> 8 & 0xFF;
                    final int b2 = rgb2 & 0xFF;
                    final int er = r1 - r2;
                    final int eg = g1 - g2;
                    final int eb = b1 - b2;
                    for (int j = -1; j <= 1; ++j) {
                        final int iy = j + y;
                        if (0 <= iy && iy < height) {
                            for (int k = -1; k <= 1; ++k) {
                                final int jx = k + x;
                                if (0 <= jx && jx < width) {
                                    int w;
                                    if (reverse) {
                                        w = QuantizeFilter.matrix[(j + 1) * 3 - k + 1];
                                    }
                                    else {
                                        w = QuantizeFilter.matrix[(j + 1) * 3 + k + 1];
                                    }
                                    if (w != 0) {
                                        final int l = reverse ? (index - k) : (index + k);
                                        rgb1 = inPixels[l];
                                        r1 = (rgb1 >> 16 & 0xFF);
                                        g1 = (rgb1 >> 8 & 0xFF);
                                        b1 = (rgb1 & 0xFF);
                                        r1 += er * w / this.sum;
                                        g1 += eg * w / this.sum;
                                        b1 += eb * w / this.sum;
                                        inPixels[l] = (PixelUtils.clamp(r1) << 16 | PixelUtils.clamp(g1) << 8 | PixelUtils.clamp(b1));
                                    }
                                }
                            }
                        }
                    }
                    index += direction;
                }
            }
        }
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final int[] outPixels = new int[width * height];
        this.quantize(inPixels, outPixels, width, height, this.numColors, this.dither, this.serpentine);
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Colors/Quantize...";
    }
    
    static {
        matrix = new int[] { 0, 0, 0, 0, 0, 7, 3, 5, 1 };
    }
}
