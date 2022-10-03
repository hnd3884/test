package com.jhlabs.image;

import java.awt.Rectangle;

public class OilFilter extends WholeImageFilter
{
    private int range;
    private int levels;
    
    public OilFilter() {
        this.range = 3;
        this.levels = 256;
    }
    
    public void setRange(final int range) {
        this.range = range;
    }
    
    public int getRange() {
        return this.range;
    }
    
    public void setLevels(final int levels) {
        this.levels = levels;
    }
    
    public int getLevels() {
        return this.levels;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final int[] rHistogram = new int[this.levels];
        final int[] gHistogram = new int[this.levels];
        final int[] bHistogram = new int[this.levels];
        final int[] rTotal = new int[this.levels];
        final int[] gTotal = new int[this.levels];
        final int[] bTotal = new int[this.levels];
        final int[] outPixels = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                for (int i = 0; i < this.levels; ++i) {
                    final int[] array = rHistogram;
                    final int n = i;
                    final int[] array2 = gHistogram;
                    final int n2 = i;
                    final int[] array3 = bHistogram;
                    final int n3 = i;
                    final int[] array4 = rTotal;
                    final int n4 = i;
                    final int[] array5 = gTotal;
                    final int n5 = i;
                    final int[] array6 = bTotal;
                    final int n6 = i;
                    final int n7 = 0;
                    array5[n5] = (array6[n6] = n7);
                    array3[n3] = (array4[n4] = n7);
                    array[n] = (array2[n2] = n7);
                }
                for (int row = -this.range; row <= this.range; ++row) {
                    final int iy = y + row;
                    if (0 <= iy && iy < height) {
                        final int ioffset = iy * width;
                        for (int col = -this.range; col <= this.range; ++col) {
                            final int ix = x + col;
                            if (0 <= ix && ix < width) {
                                final int rgb = inPixels[ioffset + ix];
                                final int r = rgb >> 16 & 0xFF;
                                final int g = rgb >> 8 & 0xFF;
                                final int b = rgb & 0xFF;
                                final int ri = r * this.levels / 256;
                                final int gi = g * this.levels / 256;
                                final int bi = b * this.levels / 256;
                                final int[] array7 = rTotal;
                                final int n8 = ri;
                                array7[n8] += r;
                                final int[] array8 = gTotal;
                                final int n9 = gi;
                                array8[n9] += g;
                                final int[] array9 = bTotal;
                                final int n10 = bi;
                                array9[n10] += b;
                                final int[] array10 = rHistogram;
                                final int n11 = ri;
                                ++array10[n11];
                                final int[] array11 = gHistogram;
                                final int n12 = gi;
                                ++array11[n12];
                                final int[] array12 = bHistogram;
                                final int n13 = bi;
                                ++array12[n13];
                            }
                        }
                    }
                }
                int r2 = 0;
                int g2 = 0;
                int b2 = 0;
                for (int j = 1; j < this.levels; ++j) {
                    if (rHistogram[j] > rHistogram[r2]) {
                        r2 = j;
                    }
                    if (gHistogram[j] > gHistogram[g2]) {
                        g2 = j;
                    }
                    if (bHistogram[j] > bHistogram[b2]) {
                        b2 = j;
                    }
                }
                r2 = rTotal[r2] / rHistogram[r2];
                g2 = gTotal[g2] / gHistogram[g2];
                b2 = bTotal[b2] / bHistogram[b2];
                outPixels[index++] = (0xFF000000 | r2 << 16 | g2 << 8 | b2);
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Stylize/Oil...";
    }
}
