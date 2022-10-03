package com.jhlabs.image;

import java.awt.Rectangle;

public class LifeFilter extends BinaryFilter
{
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final int[] outPixels = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int r = 0;
                final int g = 0;
                final int b = 0;
                final int pixel = inPixels[y * width + x];
                final int a = pixel & 0xFF000000;
                int neighbours = 0;
                for (int row = -1; row <= 1; ++row) {
                    final int iy = y + row;
                    if (0 <= iy && iy < height) {
                        final int ioffset = iy * width;
                        for (int col = -1; col <= 1; ++col) {
                            final int ix = x + col;
                            if ((row != 0 || col != 0) && 0 <= ix && ix < width) {
                                final int rgb = inPixels[ioffset + ix];
                                if (this.blackFunction.isBlack(rgb)) {
                                    ++neighbours;
                                }
                            }
                        }
                    }
                }
                if (this.blackFunction.isBlack(pixel)) {
                    outPixels[index++] = ((neighbours == 2 || neighbours == 3) ? pixel : -1);
                }
                else {
                    outPixels[index++] = ((neighbours == 3) ? -16777216 : pixel);
                }
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Binary/Life";
    }
}
