package com.jhlabs.image;

import java.awt.Rectangle;

public class MaximumFilter extends WholeImageFilter
{
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final int[] outPixels = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int pixel = -16777216;
                for (int dy = -1; dy <= 1; ++dy) {
                    final int iy = y + dy;
                    if (0 <= iy && iy < height) {
                        final int ioffset = iy * width;
                        for (int dx = -1; dx <= 1; ++dx) {
                            final int ix = x + dx;
                            if (0 <= ix && ix < width) {
                                pixel = PixelUtils.combinePixels(pixel, inPixels[ioffset + ix], 3);
                            }
                        }
                    }
                }
                outPixels[index++] = pixel;
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Blur/Maximum";
    }
}
