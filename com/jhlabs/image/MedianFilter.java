package com.jhlabs.image;

import java.awt.Rectangle;

public class MedianFilter extends WholeImageFilter
{
    private int median(final int[] array) {
        for (int i = 0; i < 4; ++i) {
            int max = 0;
            int maxIndex = 0;
            for (int j = 0; j < 9; ++j) {
                if (array[j] > max) {
                    max = array[j];
                    maxIndex = j;
                }
            }
            array[maxIndex] = 0;
        }
        int max = 0;
        for (int i = 0; i < 9; ++i) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }
    
    private int rgbMedian(final int[] r, final int[] g, final int[] b) {
        int index = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < 9; ++i) {
            int sum = 0;
            for (int j = 0; j < 9; ++j) {
                sum += Math.abs(r[i] - r[j]);
                sum += Math.abs(g[i] - g[j]);
                sum += Math.abs(b[i] - b[j]);
            }
            if (sum < min) {
                min = sum;
                index = i;
            }
        }
        return index;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final int[] argb = new int[9];
        final int[] r = new int[9];
        final int[] g = new int[9];
        final int[] b = new int[9];
        final int[] outPixels = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int k = 0;
                for (int dy = -1; dy <= 1; ++dy) {
                    final int iy = y + dy;
                    if (0 <= iy && iy < height) {
                        final int ioffset = iy * width;
                        for (int dx = -1; dx <= 1; ++dx) {
                            final int ix = x + dx;
                            if (0 <= ix && ix < width) {
                                final int rgb = inPixels[ioffset + ix];
                                r[k] = ((argb[k] = rgb) >> 16 & 0xFF);
                                g[k] = (rgb >> 8 & 0xFF);
                                b[k] = (rgb & 0xFF);
                                ++k;
                            }
                        }
                    }
                }
                while (k < 9) {
                    argb[k] = -16777216;
                    final int[] array = r;
                    final int n = k;
                    final int[] array2 = g;
                    final int n2 = k;
                    final int[] array3 = b;
                    final int n3 = k;
                    final int n4 = 0;
                    array3[n3] = n4;
                    array[n] = (array2[n2] = n4);
                    ++k;
                }
                outPixels[index++] = argb[this.rgbMedian(r, g, b)];
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Blur/Median";
    }
}
