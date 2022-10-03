package com.jhlabs.image;

public class FadeFilter extends PointFilter
{
    private int width;
    private int height;
    private float angle;
    private float fadeStart;
    private float fadeWidth;
    private int sides;
    private boolean invert;
    private float m00;
    private float m01;
    private float m10;
    private float m11;
    
    public FadeFilter() {
        this.angle = 0.0f;
        this.fadeStart = 1.0f;
        this.fadeWidth = 10.0f;
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
        final float cos = (float)Math.cos(angle);
        final float sin = (float)Math.sin(angle);
        this.m00 = cos;
        this.m01 = sin;
        this.m10 = -sin;
        this.m11 = cos;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setSides(final int sides) {
        this.sides = sides;
    }
    
    public int getSides() {
        return this.sides;
    }
    
    public void setFadeStart(final float fadeStart) {
        this.fadeStart = fadeStart;
    }
    
    public float getFadeStart() {
        return this.fadeStart;
    }
    
    public void setFadeWidth(final float fadeWidth) {
        this.fadeWidth = fadeWidth;
    }
    
    public float getFadeWidth() {
        return this.fadeWidth;
    }
    
    public void setInvert(final boolean invert) {
        this.invert = invert;
    }
    
    public boolean getInvert() {
        return this.invert;
    }
    
    @Override
    public void setDimensions(final int width, final int height) {
        super.setDimensions(this.width = width, this.height = height);
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        float nx = this.m00 * x + this.m01 * y;
        final float ny = this.m10 * x + this.m11 * y;
        if (this.sides == 2) {
            nx = (float)Math.sqrt(nx * nx + ny * ny);
        }
        else if (this.sides == 3) {
            nx = ImageMath.mod(nx, 16.0f);
        }
        else if (this.sides == 4) {
            nx = this.symmetry(nx, 16.0f);
        }
        int alpha = (int)(ImageMath.smoothStep(this.fadeStart, this.fadeStart + this.fadeWidth, nx) * 255.0f);
        if (this.invert) {
            alpha = 255 - alpha;
        }
        return alpha << 24 | (rgb & 0xFFFFFF);
    }
    
    public float symmetry(float x, final float b) {
        x = ImageMath.mod(x, 2.0f * b);
        if (x > b) {
            return 2.0f * b - x;
        }
        return x;
    }
    
    @Override
    public String toString() {
        return "Fade...";
    }
}
