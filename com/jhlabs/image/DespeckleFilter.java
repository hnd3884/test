package com.jhlabs.image;

import java.awt.Rectangle;

public class DespeckleFilter extends WholeImageFilter
{
    private short pepperAndSalt(short c, final short v1, final short v2) {
        if (c < v1) {
            ++c;
        }
        if (c < v2) {
            ++c;
        }
        if (c > v1) {
            --c;
        }
        if (c > v2) {
            --c;
        }
        return c;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final short[][] r = new short[3][width];
        final short[][] g = new short[3][width];
        final short[][] b = new short[3][width];
        final int[] outPixels = new int[width * height];
        for (int x = 0; x < width; ++x) {
            final int rgb = inPixels[x];
            r[1][x] = (short)(rgb >> 16 & 0xFF);
            g[1][x] = (short)(rgb >> 8 & 0xFF);
            b[1][x] = (short)(rgb & 0xFF);
        }
        for (int y = 0; y < height; ++y) {
            final boolean yIn = y > 0 && y < height - 1;
            int nextRowIndex = index + width;
            if (y < height - 1) {
                for (int x2 = 0; x2 < width; ++x2) {
                    final int rgb2 = inPixels[nextRowIndex++];
                    r[2][x2] = (short)(rgb2 >> 16 & 0xFF);
                    g[2][x2] = (short)(rgb2 >> 8 & 0xFF);
                    b[2][x2] = (short)(rgb2 & 0xFF);
                }
            }
            for (int x2 = 0; x2 < width; ++x2) {
                final boolean xIn = x2 > 0 && x2 < width - 1;
                short or = r[1][x2];
                short og = g[1][x2];
                short ob = b[1][x2];
                final int w = x2 - 1;
                final int e = x2 + 1;
                if (yIn) {
                    or = this.pepperAndSalt(or, r[0][x2], r[2][x2]);
                    og = this.pepperAndSalt(og, g[0][x2], g[2][x2]);
                    ob = this.pepperAndSalt(ob, b[0][x2], b[2][x2]);
                }
                if (xIn) {
                    or = this.pepperAndSalt(or, r[1][w], r[1][e]);
                    og = this.pepperAndSalt(og, g[1][w], g[1][e]);
                    ob = this.pepperAndSalt(ob, b[1][w], b[1][e]);
                }
                if (yIn && xIn) {
                    or = this.pepperAndSalt(or, r[0][w], r[2][e]);
                    og = this.pepperAndSalt(og, g[0][w], g[2][e]);
                    ob = this.pepperAndSalt(ob, b[0][w], b[2][e]);
                    or = this.pepperAndSalt(or, r[2][w], r[0][e]);
                    og = this.pepperAndSalt(og, g[2][w], g[0][e]);
                    ob = this.pepperAndSalt(ob, b[2][w], b[0][e]);
                }
                outPixels[index] = ((inPixels[index] & 0xFF000000) | or << 16 | og << 8 | ob);
                ++index;
            }
            short[] t = r[0];
            r[0] = r[1];
            r[1] = r[2];
            r[2] = t;
            t = g[0];
            g[0] = g[1];
            g[1] = g[2];
            g[2] = t;
            t = b[0];
            b[0] = b[1];
            b[1] = b[2];
            b[2] = t;
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Blur/Despeckle...";
    }
}
