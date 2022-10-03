package com.google.zxing.common;

import com.google.zxing.NotFoundException;

public abstract class GridSampler
{
    private static GridSampler gridSampler;
    
    public static void setGridSampler(final GridSampler newGridSampler) {
        GridSampler.gridSampler = newGridSampler;
    }
    
    public static GridSampler getInstance() {
        return GridSampler.gridSampler;
    }
    
    public abstract BitMatrix sampleGrid(final BitMatrix p0, final int p1, final int p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14, final float p15, final float p16, final float p17, final float p18) throws NotFoundException;
    
    public abstract BitMatrix sampleGrid(final BitMatrix p0, final int p1, final int p2, final PerspectiveTransform p3) throws NotFoundException;
    
    protected static void checkAndNudgePoints(final BitMatrix image, final float[] points) throws NotFoundException {
        final int width = image.getWidth();
        final int height = image.getHeight();
        boolean nudged = true;
        for (int offset = 0; offset < points.length && nudged; offset += 2) {
            final int x = (int)points[offset];
            final int y = (int)points[offset + 1];
            if (x < -1 || x > width || y < -1 || y > height) {
                throw NotFoundException.getNotFoundInstance();
            }
            nudged = false;
            if (x == -1) {
                points[offset] = 0.0f;
                nudged = true;
            }
            else if (x == width) {
                points[offset] = (float)(width - 1);
                nudged = true;
            }
            if (y == -1) {
                points[offset + 1] = 0.0f;
                nudged = true;
            }
            else if (y == height) {
                points[offset + 1] = (float)(height - 1);
                nudged = true;
            }
        }
        nudged = true;
        for (int offset = points.length - 2; offset >= 0 && nudged; offset -= 2) {
            final int x = (int)points[offset];
            final int y = (int)points[offset + 1];
            if (x < -1 || x > width || y < -1 || y > height) {
                throw NotFoundException.getNotFoundInstance();
            }
            nudged = false;
            if (x == -1) {
                points[offset] = 0.0f;
                nudged = true;
            }
            else if (x == width) {
                points[offset] = (float)(width - 1);
                nudged = true;
            }
            if (y == -1) {
                points[offset + 1] = 0.0f;
                nudged = true;
            }
            else if (y == height) {
                points[offset + 1] = (float)(height - 1);
                nudged = true;
            }
        }
    }
    
    static {
        GridSampler.gridSampler = new DefaultGridSampler();
    }
}
