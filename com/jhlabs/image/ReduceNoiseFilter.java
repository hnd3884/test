package com.jhlabs.image;

import java.awt.Rectangle;

public class ReduceNoiseFilter extends WholeImageFilter
{
    private int smooth(final int[] v) {
        int minindex = 0;
        int maxindex = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < 9; ++i) {
            if (i != 4) {
                if (v[i] < min) {
                    min = v[i];
                    minindex = i;
                }
                if (v[i] > max) {
                    max = v[i];
                    maxindex = i;
                }
            }
        }
        if (v[4] < min) {
            return v[minindex];
        }
        if (v[4] > max) {
            return v[maxindex];
        }
        return v[4];
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final int[] r = new int[9];
        final int[] g = new int[9];
        final int[] b = new int[9];
        final int[] outPixels = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int k = 0;
                final int irgb = inPixels[index];
                final int ir = irgb >> 16 & 0xFF;
                final int ig = irgb >> 8 & 0xFF;
                final int ib = irgb & 0xFF;
                for (int dy = -1; dy <= 1; ++dy) {
                    final int iy = y + dy;
                    if (0 <= iy && iy < height) {
                        final int ioffset = iy * width;
                        for (int dx = -1; dx <= 1; ++dx) {
                            final int ix = x + dx;
                            if (0 <= ix && ix < width) {
                                final int rgb = inPixels[ioffset + ix];
                                r[k] = (rgb >> 16 & 0xFF);
                                g[k] = (rgb >> 8 & 0xFF);
                                b[k] = (rgb & 0xFF);
                            }
                            else {
                                r[k] = ir;
                                g[k] = ig;
                                b[k] = ib;
                            }
                            ++k;
                        }
                    }
                    else {
                        for (int dx2 = -1; dx2 <= 1; ++dx2) {
                            r[k] = ir;
                            g[k] = ig;
                            b[k] = ib;
                            ++k;
                        }
                    }
                }
                outPixels[index] = ((inPixels[index] & 0xFF000000) | this.smooth(r) << 16 | this.smooth(g) << 8 | this.smooth(b));
                ++index;
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Blur/Smooth";
    }
}
