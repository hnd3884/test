package com.jhlabs.image;

import java.awt.Rectangle;

public class DiffusionFilter extends WholeImageFilter
{
    private static final int[] diffusionMatrix;
    private int[] matrix;
    private int sum;
    private boolean serpentine;
    private boolean colorDither;
    private int levels;
    
    public DiffusionFilter() {
        this.sum = 16;
        this.serpentine = true;
        this.colorDither = true;
        this.levels = 6;
        this.setMatrix(DiffusionFilter.diffusionMatrix);
    }
    
    public void setSerpentine(final boolean serpentine) {
        this.serpentine = serpentine;
    }
    
    public boolean getSerpentine() {
        return this.serpentine;
    }
    
    public void setColorDither(final boolean colorDither) {
        this.colorDither = colorDither;
    }
    
    public boolean getColorDither() {
        return this.colorDither;
    }
    
    public void setMatrix(final int[] matrix) {
        this.matrix = matrix;
        this.sum = 0;
        for (int i = 0; i < matrix.length; ++i) {
            this.sum += matrix[i];
        }
    }
    
    public int[] getMatrix() {
        return this.matrix;
    }
    
    public void setLevels(final int levels) {
        this.levels = levels;
    }
    
    public int getLevels() {
        return this.levels;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final int[] outPixels = new int[width * height];
        int index = 0;
        final int[] map = new int[this.levels];
        for (int i = 0; i < this.levels; ++i) {
            final int v = 255 * i / (this.levels - 1);
            map[i] = v;
        }
        final int[] div = new int[256];
        for (int j = 0; j < 256; ++j) {
            div[j] = this.levels * j / 256;
        }
        for (int y = 0; y < height; ++y) {
            final boolean reverse = this.serpentine && (y & 0x1) == 0x1;
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
                int r1 = rgb1 >> 16 & 0xFF;
                int g1 = rgb1 >> 8 & 0xFF;
                int b1 = rgb1 & 0xFF;
                if (!this.colorDither) {
                    g1 = (r1 = (b1 = (r1 + g1 + b1) / 3));
                }
                final int r2 = map[div[r1]];
                final int g2 = map[div[g1]];
                final int b2 = map[div[b1]];
                outPixels[index] = (0xFF000000 | r2 << 16 | g2 << 8 | b2);
                final int er = r1 - r2;
                final int eg = g1 - g2;
                final int eb = b1 - b2;
                for (int k = -1; k <= 1; ++k) {
                    final int iy = k + y;
                    if (0 <= iy && iy < height) {
                        for (int l = -1; l <= 1; ++l) {
                            final int jx = l + x;
                            if (0 <= jx && jx < width) {
                                int w;
                                if (reverse) {
                                    w = this.matrix[(k + 1) * 3 - l + 1];
                                }
                                else {
                                    w = this.matrix[(k + 1) * 3 + l + 1];
                                }
                                if (w != 0) {
                                    final int m = reverse ? (index - l) : (index + l);
                                    rgb1 = inPixels[m];
                                    r1 = (rgb1 >> 16 & 0xFF);
                                    g1 = (rgb1 >> 8 & 0xFF);
                                    b1 = (rgb1 & 0xFF);
                                    r1 += er * w / this.sum;
                                    g1 += eg * w / this.sum;
                                    b1 += eb * w / this.sum;
                                    inPixels[m] = (PixelUtils.clamp(r1) << 16 | PixelUtils.clamp(g1) << 8 | PixelUtils.clamp(b1));
                                }
                            }
                        }
                    }
                }
                index += direction;
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Colors/Diffusion Dither...";
    }
    
    static {
        diffusionMatrix = new int[] { 0, 0, 0, 0, 0, 7, 3, 5, 1 };
    }
}
