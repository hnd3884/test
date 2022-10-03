package com.jhlabs.image;

public class WeaveFilter extends PointFilter
{
    private float xWidth;
    private float yWidth;
    private float xGap;
    private float yGap;
    private int rows;
    private int cols;
    private int rgbX;
    private int rgbY;
    private boolean useImageColors;
    private boolean roundThreads;
    private boolean shadeCrossings;
    public int[][] matrix;
    
    public WeaveFilter() {
        this.xWidth = 16.0f;
        this.yWidth = 16.0f;
        this.xGap = 6.0f;
        this.yGap = 6.0f;
        this.rows = 4;
        this.cols = 4;
        this.rgbX = -32640;
        this.rgbY = -8355585;
        this.useImageColors = true;
        this.roundThreads = false;
        this.shadeCrossings = true;
        this.matrix = new int[][] { { 0, 1, 0, 1 }, { 1, 0, 1, 0 }, { 0, 1, 0, 1 }, { 1, 0, 1, 0 } };
    }
    
    public void setXGap(final float xGap) {
        this.xGap = xGap;
    }
    
    public void setXWidth(final float xWidth) {
        this.xWidth = xWidth;
    }
    
    public float getXWidth() {
        return this.xWidth;
    }
    
    public void setYWidth(final float yWidth) {
        this.yWidth = yWidth;
    }
    
    public float getYWidth() {
        return this.yWidth;
    }
    
    public float getXGap() {
        return this.xGap;
    }
    
    public void setYGap(final float yGap) {
        this.yGap = yGap;
    }
    
    public float getYGap() {
        return this.yGap;
    }
    
    public void setCrossings(final int[][] matrix) {
        this.matrix = matrix;
    }
    
    public int[][] getCrossings() {
        return this.matrix;
    }
    
    public void setUseImageColors(final boolean useImageColors) {
        this.useImageColors = useImageColors;
    }
    
    public boolean getUseImageColors() {
        return this.useImageColors;
    }
    
    public void setRoundThreads(final boolean roundThreads) {
        this.roundThreads = roundThreads;
    }
    
    public boolean getRoundThreads() {
        return this.roundThreads;
    }
    
    public void setShadeCrossings(final boolean shadeCrossings) {
        this.shadeCrossings = shadeCrossings;
    }
    
    public boolean getShadeCrossings() {
        return this.shadeCrossings;
    }
    
    @Override
    public int filterRGB(int x, int y, final int rgb) {
        x += (int)(this.xWidth + this.xGap / 2.0f);
        y += (int)(this.yWidth + this.yGap / 2.0f);
        final float nx = ImageMath.mod((float)x, this.xWidth + this.xGap);
        final float ny = ImageMath.mod((float)y, this.yWidth + this.yGap);
        final int ix = (int)(x / (this.xWidth + this.xGap));
        final int iy = (int)(y / (this.yWidth + this.yGap));
        final boolean inX = nx < this.xWidth;
        final boolean inY = ny < this.yWidth;
        float dX;
        float dY;
        if (this.roundThreads) {
            dX = Math.abs(this.xWidth / 2.0f - nx) / this.xWidth / 2.0f;
            dY = Math.abs(this.yWidth / 2.0f - ny) / this.yWidth / 2.0f;
        }
        else {
            dY = (dX = 0.0f);
        }
        float cX;
        float cY;
        if (this.shadeCrossings) {
            cX = ImageMath.smoothStep(this.xWidth / 2.0f, this.xWidth / 2.0f + this.xGap, Math.abs(this.xWidth / 2.0f - nx));
            cY = ImageMath.smoothStep(this.yWidth / 2.0f, this.yWidth / 2.0f + this.yGap, Math.abs(this.yWidth / 2.0f - ny));
        }
        else {
            cY = (cX = 0.0f);
        }
        int lrgbY;
        int lrgbX;
        if (this.useImageColors) {
            lrgbY = rgb;
            lrgbX = rgb;
        }
        else {
            lrgbX = this.rgbX;
            lrgbY = this.rgbY;
        }
        final int ixc = ix % this.cols;
        final int iyr = iy % this.rows;
        final int m = this.matrix[iyr][ixc];
        int v;
        if (inX) {
            if (inY) {
                v = ((m == 1) ? lrgbX : lrgbY);
                v = ImageMath.mixColors(2.0f * ((m == 1) ? dX : dY), v, -16777216);
            }
            else {
                if (this.shadeCrossings) {
                    if (m != this.matrix[(iy + 1) % this.rows][ixc]) {
                        if (m == 0) {
                            cY = 1.0f - cY;
                        }
                        cY *= 0.5f;
                        lrgbX = ImageMath.mixColors(cY, lrgbX, -16777216);
                    }
                    else if (m == 0) {
                        lrgbX = ImageMath.mixColors(0.5f, lrgbX, -16777216);
                    }
                }
                v = ImageMath.mixColors(2.0f * dX, lrgbX, -16777216);
            }
        }
        else if (inY) {
            if (this.shadeCrossings) {
                if (m != this.matrix[iyr][(ix + 1) % this.cols]) {
                    if (m == 1) {
                        cX = 1.0f - cX;
                    }
                    cX *= 0.5f;
                    lrgbY = ImageMath.mixColors(cX, lrgbY, -16777216);
                }
                else if (m == 1) {
                    lrgbY = ImageMath.mixColors(0.5f, lrgbY, -16777216);
                }
            }
            v = ImageMath.mixColors(2.0f * dY, lrgbY, -16777216);
        }
        else {
            v = 0;
        }
        return v;
    }
    
    @Override
    public String toString() {
        return "Texture/Weave...";
    }
}
