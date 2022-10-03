package com.jhlabs.image;

public class WarpGrid
{
    public float[] xGrid;
    public float[] yGrid;
    public int rows;
    public int cols;
    private static final float m00 = -0.5f;
    private static final float m01 = 1.5f;
    private static final float m02 = -1.5f;
    private static final float m03 = 0.5f;
    private static final float m10 = 1.0f;
    private static final float m11 = -2.5f;
    private static final float m12 = 2.0f;
    private static final float m13 = -0.5f;
    private static final float m20 = -0.5f;
    private static final float m22 = 0.5f;
    private static final float m31 = 1.0f;
    
    public WarpGrid(final int rows, final int cols, final int w, final int h) {
        this.xGrid = null;
        this.yGrid = null;
        this.rows = rows;
        this.cols = cols;
        this.xGrid = new float[rows * cols];
        this.yGrid = new float[rows * cols];
        int index = 0;
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                this.xGrid[index] = col * (float)(w - 1) / (cols - 1);
                this.yGrid[index] = row * (float)(h - 1) / (rows - 1);
                ++index;
            }
        }
    }
    
    public void addRow(final int before) {
        final int size = (this.rows + 1) * this.cols;
        final float[] x = new float[size];
        final float[] y = new float[size];
        ++this.rows;
        int i = 0;
        int j = 0;
        for (int row = 0; row < this.rows; ++row) {
            for (int col = 0; col < this.cols; ++col) {
                final int k = j + col;
                final int l = i + col;
                if (row == before) {
                    x[k] = (this.xGrid[l] + this.xGrid[k]) / 2.0f;
                    y[k] = (this.yGrid[l] + this.yGrid[k]) / 2.0f;
                }
                else {
                    x[k] = this.xGrid[l];
                    y[k] = this.yGrid[l];
                }
            }
            if (row != before - 1) {
                i += this.cols;
            }
            j += this.cols;
        }
        this.xGrid = x;
        this.yGrid = y;
    }
    
    public void addCol(final int before) {
        final int size = this.rows * (this.cols + 1);
        final float[] x = new float[size];
        final float[] y = new float[size];
        ++this.cols;
        int i = 0;
        int j = 0;
        for (int row = 0; row < this.rows; ++row) {
            for (int col = 0; col < this.cols; ++col) {
                if (col == before) {
                    x[j] = (this.xGrid[i] + this.xGrid[i - 1]) / 2.0f;
                    y[j] = (this.yGrid[i] + this.yGrid[i - 1]) / 2.0f;
                }
                else {
                    x[j] = this.xGrid[i];
                    y[j] = this.yGrid[i];
                    ++i;
                }
                ++j;
            }
        }
        this.xGrid = x;
        this.yGrid = y;
    }
    
    public void removeRow(final int r) {
        final int size = (this.rows - 1) * this.cols;
        final float[] x = new float[size];
        final float[] y = new float[size];
        --this.rows;
        int i = 0;
        int j = 0;
        for (int row = 0; row < this.rows; ++row) {
            for (int col = 0; col < this.cols; ++col) {
                final int k = j + col;
                final int l = i + col;
                x[k] = this.xGrid[l];
                y[k] = this.yGrid[l];
            }
            if (row == r - 1) {
                i += this.cols;
            }
            i += this.cols;
            j += this.cols;
        }
        this.xGrid = x;
        this.yGrid = y;
    }
    
    public void removeCol(final int r) {
        final int size = this.rows * (this.cols + 1);
        final float[] x = new float[size];
        final float[] y = new float[size];
        --this.cols;
        for (int row = 0; row < this.rows; ++row) {
            int i = row * (this.cols + 1);
            int j = row * this.cols;
            for (int col = 0; col < this.cols; ++col) {
                x[j] = this.xGrid[i];
                y[j] = this.yGrid[i];
                if (col == r - 1) {
                    ++i;
                }
                ++i;
                ++j;
            }
        }
        this.xGrid = x;
        this.yGrid = y;
    }
    
    public void lerp(final float t, final WarpGrid destination, final WarpGrid intermediate) {
        if (this.rows != destination.rows || this.cols != destination.cols) {
            throw new IllegalArgumentException("source and destination are different sizes");
        }
        if (this.rows != intermediate.rows || this.cols != intermediate.cols) {
            throw new IllegalArgumentException("source and intermediate are different sizes");
        }
        int index = 0;
        for (int row = 0; row < this.rows; ++row) {
            for (int col = 0; col < this.cols; ++col) {
                intermediate.xGrid[index] = ImageMath.lerp(t, this.xGrid[index], destination.xGrid[index]);
                intermediate.yGrid[index] = ImageMath.lerp(t, this.yGrid[index], destination.yGrid[index]);
                ++index;
            }
        }
    }
    
    public void warp(final int[] inPixels, final int cols, final int rows, final WarpGrid sourceGrid, final WarpGrid destGrid, final int[] outPixels) {
        try {
            if (sourceGrid.rows != destGrid.rows || sourceGrid.cols != destGrid.cols) {
                throw new IllegalArgumentException("source and destination grids are different sizes");
            }
            final int size = Math.max(cols, rows);
            final float[] xrow = new float[size];
            final float[] yrow = new float[size];
            final float[] scale = new float[size + 1];
            final float[] interpolated = new float[size + 1];
            final int gridCols = sourceGrid.cols;
            final int gridRows = sourceGrid.rows;
            WarpGrid splines = new WarpGrid(rows, gridCols, 1, 1);
            for (int u = 0; u < gridCols; ++u) {
                int i = u;
                for (int v = 0; v < gridRows; ++v) {
                    xrow[v] = sourceGrid.xGrid[i];
                    yrow[v] = sourceGrid.yGrid[i];
                    i += gridCols;
                }
                this.interpolateSpline(yrow, xrow, 0, gridRows, interpolated, 0, rows);
                i = u;
                for (int y = 0; y < rows; ++y) {
                    splines.xGrid[i] = interpolated[y];
                    i += gridCols;
                }
            }
            for (int u = 0; u < gridCols; ++u) {
                int i = u;
                for (int v = 0; v < gridRows; ++v) {
                    xrow[v] = destGrid.xGrid[i];
                    yrow[v] = destGrid.yGrid[i];
                    i += gridCols;
                }
                this.interpolateSpline(yrow, xrow, 0, gridRows, interpolated, 0, rows);
                i = u;
                for (int y = 0; y < rows; ++y) {
                    splines.yGrid[i] = interpolated[y];
                    i += gridCols;
                }
            }
            final int[] intermediate = new int[rows * cols];
            int offset = 0;
            for (int y = 0; y < rows; ++y) {
                this.interpolateSpline(splines.xGrid, splines.yGrid, offset, gridCols, scale, 0, cols);
                scale[cols] = (float)cols;
                ImageMath.resample(inPixels, intermediate, cols, y * cols, 1, scale);
                offset += gridCols;
            }
            splines = new WarpGrid(gridRows, cols, 1, 1);
            offset = 0;
            int offset2 = 0;
            for (int v = 0; v < gridRows; ++v) {
                this.interpolateSpline(sourceGrid.xGrid, sourceGrid.yGrid, offset, gridCols, splines.xGrid, offset2, cols);
                offset += gridCols;
                offset2 += cols;
            }
            offset = 0;
            offset2 = 0;
            for (int v = 0; v < gridRows; ++v) {
                this.interpolateSpline(destGrid.xGrid, destGrid.yGrid, offset, gridCols, splines.yGrid, offset2, cols);
                offset += gridCols;
                offset2 += cols;
            }
            for (int x = 0; x < cols; ++x) {
                int j = x;
                for (int v = 0; v < gridRows; ++v) {
                    xrow[v] = splines.xGrid[j];
                    yrow[v] = splines.yGrid[j];
                    j += cols;
                }
                this.interpolateSpline(xrow, yrow, 0, gridRows, scale, 0, rows);
                scale[rows] = (float)rows;
                ImageMath.resample(intermediate, outPixels, rows, x, cols, scale);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void interpolateSpline(final float[] xKnots, final float[] yKnots, final int offset, final int length, final float[] splineY, final int splineOffset, final int splineLength) {
        int index = offset;
        final int end = offset + length - 1;
        float x0 = xKnots[index];
        float k3;
        float k2;
        float k1 = k2 = (k3 = yKnots[index]);
        float x2 = xKnots[index + 1];
        float k4 = yKnots[index + 1];
        for (int i = 0; i < splineLength; ++i) {
            if (index <= end && i > xKnots[index]) {
                k2 = k1;
                k1 = k3;
                k3 = k4;
                x0 = xKnots[index];
                if (++index <= end) {
                    x2 = xKnots[index];
                }
                if (index < end) {
                    k4 = yKnots[index + 1];
                }
                else {
                    k4 = k3;
                }
            }
            final float t = (i - x0) / (x2 - x0);
            final float c3 = -0.5f * k2 + 1.5f * k1 + -1.5f * k3 + 0.5f * k4;
            final float c4 = 1.0f * k2 + -2.5f * k1 + 2.0f * k3 + -0.5f * k4;
            final float c5 = -0.5f * k2 + 0.5f * k3;
            final float c6 = 1.0f * k1;
            splineY[splineOffset + i] = ((c3 * t + c4) * t + c5) * t + c6;
        }
    }
    
    protected void interpolateSpline2(final float[] xKnots, final float[] yKnots, final int offset, final float[] splineY, final int splineOffset, final int splineLength) {
        int index = offset;
        float leftX = xKnots[index];
        float leftY = yKnots[index];
        float rightX = xKnots[index + 1];
        float rightY = yKnots[index + 1];
        for (int i = 0; i < splineLength; ++i) {
            if (i > xKnots[index]) {
                leftX = xKnots[index];
                leftY = yKnots[index];
                ++index;
                rightX = xKnots[index];
                rightY = yKnots[index];
            }
            final float f = (i - leftX) / (rightX - leftX);
            splineY[splineOffset + i] = leftY + f * (rightY - leftY);
        }
    }
}
