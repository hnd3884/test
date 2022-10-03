package com.jhlabs.image;

import java.awt.Rectangle;

public class EmbossFilter extends WholeImageFilter
{
    private static final float pixelScale = 255.9f;
    private float azimuth;
    private float elevation;
    private boolean emboss;
    private float width45;
    
    public EmbossFilter() {
        this.azimuth = 2.3561945f;
        this.elevation = 0.5235988f;
        this.emboss = false;
        this.width45 = 3.0f;
    }
    
    public void setAzimuth(final float azimuth) {
        this.azimuth = azimuth;
    }
    
    public float getAzimuth() {
        return this.azimuth;
    }
    
    public void setElevation(final float elevation) {
        this.elevation = elevation;
    }
    
    public float getElevation() {
        return this.elevation;
    }
    
    public void setBumpHeight(final float bumpHeight) {
        this.width45 = 3.0f * bumpHeight;
    }
    
    public float getBumpHeight() {
        return this.width45 / 3.0f;
    }
    
    public void setEmboss(final boolean emboss) {
        this.emboss = emboss;
    }
    
    public boolean getEmboss() {
        return this.emboss;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        int index = 0;
        final int[] outPixels = new int[width * height];
        final int bumpMapWidth = width;
        final int bumpMapHeight = height;
        final int[] bumpPixels = new int[bumpMapWidth * bumpMapHeight];
        for (int i = 0; i < inPixels.length; ++i) {
            bumpPixels[i] = PixelUtils.brightness(inPixels[i]);
        }
        final int Lx = (int)(Math.cos(this.azimuth) * Math.cos(this.elevation) * 255.89999389648438);
        final int Ly = (int)(Math.sin(this.azimuth) * Math.cos(this.elevation) * 255.89999389648438);
        final int Lz = (int)(Math.sin(this.elevation) * 255.89999389648438);
        final int Nz = (int)(1530.0f / this.width45);
        final int Nz2 = Nz * Nz;
        final int NzLz = Nz * Lz;
        final int background = Lz;
        for (int bumpIndex = 0, y = 0; y < height; ++y, bumpIndex += bumpMapWidth) {
            for (int s1 = bumpIndex, s2 = s1 + bumpMapWidth, s3 = s2 + bumpMapWidth, x = 0; x < width; ++x, ++s1, ++s2, ++s3) {
                int shade;
                if (y != 0 && y < height - 2 && x != 0 && x < width - 2) {
                    final int Nx = bumpPixels[s1 - 1] + bumpPixels[s2 - 1] + bumpPixels[s3 - 1] - bumpPixels[s1 + 1] - bumpPixels[s2 + 1] - bumpPixels[s3 + 1];
                    final int Ny = bumpPixels[s3 - 1] + bumpPixels[s3] + bumpPixels[s3 + 1] - bumpPixels[s1 - 1] - bumpPixels[s1] - bumpPixels[s1 + 1];
                    if (Nx == 0 && Ny == 0) {
                        shade = background;
                    }
                    else {
                        final int NdotL;
                        if ((NdotL = Nx * Lx + Ny * Ly + NzLz) < 0) {
                            shade = 0;
                        }
                        else {
                            shade = (int)(NdotL / Math.sqrt(Nx * Nx + Ny * Ny + Nz2));
                        }
                    }
                }
                else {
                    shade = background;
                }
                if (this.emboss) {
                    final int rgb = inPixels[index];
                    final int a = rgb & 0xFF000000;
                    int r = rgb >> 16 & 0xFF;
                    int g = rgb >> 8 & 0xFF;
                    int b = rgb & 0xFF;
                    r = r * shade >> 8;
                    g = g * shade >> 8;
                    b = b * shade >> 8;
                    outPixels[index++] = (a | r << 16 | g << 8 | b);
                }
                else {
                    outPixels[index++] = (0xFF000000 | shade << 16 | shade << 8 | shade);
                }
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Stylize/Emboss...";
    }
}
