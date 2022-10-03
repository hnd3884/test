package com.jhlabs.image;

import java.awt.Rectangle;

public class ContourFilter extends WholeImageFilter
{
    private float levels;
    private float scale;
    private float offset;
    private int contourColor;
    
    public ContourFilter() {
        this.levels = 5.0f;
        this.scale = 1.0f;
        this.offset = 0.0f;
        this.contourColor = -16777216;
    }
    
    public void setLevels(final float levels) {
        this.levels = levels;
    }
    
    public float getLevels() {
        return this.levels;
    }
    
    public void setScale(final float scale) {
        this.scale = scale;
    }
    
    public float getScale() {
        return this.scale;
    }
    
    public void setOffset(final float offset) {
        this.offset = offset;
    }
    
    public float getOffset() {
        return this.offset;
    }
    
    public void setContourColor(final int contourColor) {
        this.contourColor = contourColor;
    }
    
    public int getContourColor() {
        return this.contourColor;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final short[][] r = new short[3][width];
        final int[] outPixels = new int[width * height];
        final short[] table = new short[256];
        final int offsetl = (int)(this.offset * 256.0f / this.levels);
        for (int i = 0; i < 256; ++i) {
            table[i] = (short)PixelUtils.clamp((int)(255.0 * Math.floor(this.levels * (i + offsetl) / 256.0f) / (this.levels - 1.0f) - offsetl));
        }
        for (int x = 0; x < width; ++x) {
            final int rgb = inPixels[x];
            r[1][x] = (short)PixelUtils.brightness(rgb);
        }
        for (int y = 0; y < height; ++y) {
            final boolean yIn = y > 0 && y < height - 1;
            int nextRowIndex = index + width;
            if (y < height - 1) {
                for (int x2 = 0; x2 < width; ++x2) {
                    final int rgb2 = inPixels[nextRowIndex++];
                    r[2][x2] = (short)PixelUtils.brightness(rgb2);
                }
            }
            for (int x2 = 0; x2 < width; ++x2) {
                final boolean xIn = x2 > 0 && x2 < width - 1;
                final int w = x2 - 1;
                final int e = x2 + 1;
                int v = 0;
                if (yIn && xIn) {
                    final short nwb = r[0][w];
                    final short neb = r[0][x2];
                    final short swb = r[1][w];
                    final short seb = r[1][x2];
                    final short nw = table[nwb];
                    final short ne = table[neb];
                    final short sw = table[swb];
                    final short se = table[seb];
                    if (nw != ne || nw != sw || ne != se || sw != se) {
                        v = (int)(this.scale * (Math.abs(nwb - neb) + Math.abs(nwb - swb) + Math.abs(neb - seb) + Math.abs(swb - seb)));
                        if (v > 255) {
                            v = 255;
                        }
                    }
                }
                if (v != 0) {
                    outPixels[index] = PixelUtils.combinePixels(inPixels[index], this.contourColor, 1, v);
                }
                else {
                    outPixels[index] = inPixels[index];
                }
                ++index;
            }
            final short[] t = r[0];
            r[0] = r[1];
            r[1] = r[2];
            r[2] = t;
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Stylize/Contour...";
    }
}
