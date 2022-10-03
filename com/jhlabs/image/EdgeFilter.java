package com.jhlabs.image;

import java.awt.Rectangle;

public class EdgeFilter extends WholeImageFilter
{
    public static final float R2;
    public static final float[] ROBERTS_V;
    public static final float[] ROBERTS_H;
    public static final float[] PREWITT_V;
    public static final float[] PREWITT_H;
    public static final float[] SOBEL_V;
    public static float[] SOBEL_H;
    public static final float[] FREI_CHEN_V;
    public static float[] FREI_CHEN_H;
    protected float[] vEdgeMatrix;
    protected float[] hEdgeMatrix;
    
    public EdgeFilter() {
        this.vEdgeMatrix = EdgeFilter.SOBEL_V;
        this.hEdgeMatrix = EdgeFilter.SOBEL_H;
    }
    
    public void setVEdgeMatrix(final float[] vEdgeMatrix) {
        this.vEdgeMatrix = vEdgeMatrix;
    }
    
    public float[] getVEdgeMatrix() {
        return this.vEdgeMatrix;
    }
    
    public void setHEdgeMatrix(final float[] hEdgeMatrix) {
        this.hEdgeMatrix = hEdgeMatrix;
    }
    
    public float[] getHEdgeMatrix() {
        return this.hEdgeMatrix;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final int[] outPixels = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int r = 0;
                int g = 0;
                int b = 0;
                int rh = 0;
                int gh = 0;
                int bh = 0;
                int rv = 0;
                int gv = 0;
                int bv = 0;
                final int a = inPixels[y * width + x] & 0xFF000000;
                for (int row = -1; row <= 1; ++row) {
                    final int iy = y + row;
                    int ioffset;
                    if (0 <= iy && iy < height) {
                        ioffset = iy * width;
                    }
                    else {
                        ioffset = y * width;
                    }
                    final int moffset = 3 * (row + 1) + 1;
                    for (int col = -1; col <= 1; ++col) {
                        int ix = x + col;
                        if (0 > ix || ix >= width) {
                            ix = x;
                        }
                        final int rgb = inPixels[ioffset + ix];
                        final float h = this.hEdgeMatrix[moffset + col];
                        final float v = this.vEdgeMatrix[moffset + col];
                        r = (rgb & 0xFF0000) >> 16;
                        g = (rgb & 0xFF00) >> 8;
                        b = (rgb & 0xFF);
                        rh += (int)(h * r);
                        gh += (int)(h * g);
                        bh += (int)(h * b);
                        rv += (int)(v * r);
                        gv += (int)(v * g);
                        bv += (int)(v * b);
                    }
                }
                r = (int)(Math.sqrt(rh * rh + rv * rv) / 1.8);
                g = (int)(Math.sqrt(gh * gh + gv * gv) / 1.8);
                b = (int)(Math.sqrt(bh * bh + bv * bv) / 1.8);
                r = PixelUtils.clamp(r);
                g = PixelUtils.clamp(g);
                b = PixelUtils.clamp(b);
                outPixels[index++] = (a | r << 16 | g << 8 | b);
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Blur/Detect Edges";
    }
    
    static {
        R2 = (float)Math.sqrt(2.0);
        ROBERTS_V = new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f };
        ROBERTS_H = new float[] { -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f };
        PREWITT_V = new float[] { -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f };
        PREWITT_H = new float[] { -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f };
        SOBEL_V = new float[] { -1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f };
        EdgeFilter.SOBEL_H = new float[] { -1.0f, -2.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f };
        FREI_CHEN_V = new float[] { -1.0f, 0.0f, 1.0f, -EdgeFilter.R2, 0.0f, EdgeFilter.R2, -1.0f, 0.0f, 1.0f };
        EdgeFilter.FREI_CHEN_H = new float[] { -1.0f, -EdgeFilter.R2, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, EdgeFilter.R2, 1.0f };
    }
}
