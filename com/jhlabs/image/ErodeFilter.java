package com.jhlabs.image;

import java.awt.Rectangle;

public class ErodeFilter extends BinaryFilter
{
    private int threshold;
    
    public ErodeFilter() {
        this.threshold = 2;
        this.newColor = -1;
    }
    
    public void setThreshold(final int threshold) {
        this.threshold = threshold;
    }
    
    public int getThreshold() {
        return this.threshold;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, int[] inPixels, final Rectangle transformedSpace) {
        int[] outPixels = new int[width * height];
        for (int i = 0; i < this.iterations; ++i) {
            int index = 0;
            if (i > 0) {
                final int[] t = inPixels;
                inPixels = outPixels;
                outPixels = t;
            }
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int pixel = inPixels[y * width + x];
                    if (this.blackFunction.isBlack(pixel)) {
                        int neighbours = 0;
                        for (int dy = -1; dy <= 1; ++dy) {
                            final int iy = y + dy;
                            if (0 <= iy && iy < height) {
                                final int ioffset = iy * width;
                                for (int dx = -1; dx <= 1; ++dx) {
                                    final int ix = x + dx;
                                    if ((dy != 0 || dx != 0) && 0 <= ix && ix < width) {
                                        final int rgb = inPixels[ioffset + ix];
                                        if (!this.blackFunction.isBlack(rgb)) {
                                            ++neighbours;
                                        }
                                    }
                                }
                            }
                        }
                        if (neighbours >= this.threshold) {
                            if (this.colormap != null) {
                                pixel = this.colormap.getColor(i / (float)this.iterations);
                            }
                            else {
                                pixel = this.newColor;
                            }
                        }
                    }
                    outPixels[index++] = pixel;
                }
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Binary/Erode...";
    }
}
