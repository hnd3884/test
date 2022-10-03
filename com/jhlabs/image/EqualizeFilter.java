package com.jhlabs.image;

import java.awt.Rectangle;

public class EqualizeFilter extends WholeImageFilter
{
    private int[][] lut;
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final Histogram histogram = new Histogram(inPixels, width, height, 0, width);
        if (histogram.getNumSamples() > 0) {
            final float scale = 255.0f / histogram.getNumSamples();
            this.lut = new int[3][256];
            for (int i = 0; i < 3; ++i) {
                this.lut[i][0] = histogram.getFrequency(i, 0);
                for (int j = 1; j < 256; ++j) {
                    this.lut[i][j] = this.lut[i][j - 1] + histogram.getFrequency(i, j);
                }
                for (int j = 0; j < 256; ++j) {
                    this.lut[i][j] = Math.round(this.lut[i][j] * scale);
                }
            }
        }
        else {
            this.lut = null;
        }
        int i = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                inPixels[i] = this.filterRGB(x, y, inPixels[i]);
                ++i;
            }
        }
        this.lut = null;
        return inPixels;
    }
    
    private int filterRGB(final int x, final int y, final int rgb) {
        if (this.lut != null) {
            final int a = rgb & 0xFF000000;
            final int r = this.lut[0][rgb >> 16 & 0xFF];
            final int g = this.lut[1][rgb >> 8 & 0xFF];
            final int b = this.lut[2][rgb & 0xFF];
            return a | r << 16 | g << 8 | b;
        }
        return rgb;
    }
    
    @Override
    public String toString() {
        return "Colors/Equalize";
    }
}
