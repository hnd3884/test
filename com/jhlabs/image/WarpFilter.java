package com.jhlabs.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class WarpFilter extends WholeImageFilter
{
    private WarpGrid sourceGrid;
    private WarpGrid destGrid;
    private int frames;
    private BufferedImage morphImage;
    private float time;
    
    public WarpFilter() {
        this.frames = 1;
    }
    
    public WarpFilter(final WarpGrid sourceGrid, final WarpGrid destGrid) {
        this.frames = 1;
        this.sourceGrid = sourceGrid;
        this.destGrid = destGrid;
    }
    
    public void setSourceGrid(final WarpGrid sourceGrid) {
        this.sourceGrid = sourceGrid;
    }
    
    public WarpGrid getSourceGrid() {
        return this.sourceGrid;
    }
    
    public void setDestGrid(final WarpGrid destGrid) {
        this.destGrid = destGrid;
    }
    
    public WarpGrid getDestGrid() {
        return this.destGrid;
    }
    
    public void setFrames(final int frames) {
        this.frames = frames;
    }
    
    public int getFrames() {
        return this.frames;
    }
    
    public void setMorphImage(final BufferedImage morphImage) {
        this.morphImage = morphImage;
    }
    
    public BufferedImage getMorphImage() {
        return this.morphImage;
    }
    
    public void setTime(final float time) {
        this.time = time;
    }
    
    public float getTime() {
        return this.time;
    }
    
    @Override
    protected void transformSpace(final Rectangle r) {
        r.width *= this.frames;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final int[] outPixels = new int[width * height];
        if (this.morphImage != null) {
            final int[] morphPixels = this.getRGB(this.morphImage, 0, 0, width, height, null);
            this.morph(inPixels, morphPixels, outPixels, this.sourceGrid, this.destGrid, width, height, this.time);
        }
        else if (this.frames <= 1) {
            this.sourceGrid.warp(inPixels, width, height, this.sourceGrid, this.destGrid, outPixels);
        }
        else {
            final WarpGrid newGrid = new WarpGrid(this.sourceGrid.rows, this.sourceGrid.cols, width, height);
            for (int i = 0; i < this.frames; ++i) {
                final float t = i / (float)(this.frames - 1);
                this.sourceGrid.lerp(t, this.destGrid, newGrid);
                this.sourceGrid.warp(inPixels, width, height, this.sourceGrid, newGrid, outPixels);
            }
        }
        return outPixels;
    }
    
    public void morph(final int[] srcPixels, final int[] destPixels, final int[] outPixels, final WarpGrid srcGrid, final WarpGrid destGrid, final int width, final int height, final float t) {
        final WarpGrid newGrid = new WarpGrid(srcGrid.rows, srcGrid.cols, width, height);
        srcGrid.lerp(t, destGrid, newGrid);
        srcGrid.warp(srcPixels, width, height, srcGrid, newGrid, outPixels);
        final int[] destPixels2 = new int[width * height];
        destGrid.warp(destPixels, width, height, destGrid, newGrid, destPixels2);
        this.crossDissolve(outPixels, destPixels2, width, height, t);
    }
    
    public void crossDissolve(final int[] pixels1, final int[] pixels2, final int width, final int height, final float t) {
        int index = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                pixels1[index] = ImageMath.mixColors(t, pixels1[index], pixels2[index]);
                ++index;
            }
        }
    }
    
    @Override
    public String toString() {
        return "Distort/Mesh Warp...";
    }
}
