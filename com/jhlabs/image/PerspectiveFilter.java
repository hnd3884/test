package com.jhlabs.image;

import java.awt.Rectangle;

public class PerspectiveFilter extends TransformFilter
{
    private float x0;
    private float y0;
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private float x3;
    private float y3;
    private float dx1;
    private float dy1;
    private float dx2;
    private float dy2;
    private float dx3;
    private float dy3;
    private float A;
    private float B;
    private float C;
    private float D;
    private float E;
    private float F;
    private float G;
    private float H;
    private float I;
    
    public PerspectiveFilter() {
        this(0.0f, 0.0f, 100.0f, 0.0f, 100.0f, 100.0f, 0.0f, 100.0f);
    }
    
    public PerspectiveFilter(final float x0, final float y0, final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        this.setCorners(x0, y0, x1, y1, x2, y2, x3, y3);
    }
    
    public void setCorners(final float x0, final float y0, final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.dx1 = x1 - x2;
        this.dy1 = y1 - y2;
        this.dx2 = x3 - x2;
        this.dy2 = y3 - y2;
        this.dx3 = x0 - x1 + x2 - x3;
        this.dy3 = y0 - y1 + y2 - y3;
        float a11;
        float a12;
        float a13;
        float a14;
        float a15;
        float a16;
        float a18;
        float a17;
        if (this.dx3 == 0.0f && this.dy3 == 0.0f) {
            a11 = x1 - x0;
            a12 = x2 - x1;
            a13 = x0;
            a14 = y1 - y0;
            a15 = y2 - y1;
            a16 = y0;
            a17 = (a18 = 0.0f);
        }
        else {
            a18 = (this.dx3 * this.dy2 - this.dx2 * this.dy3) / (this.dx1 * this.dy2 - this.dy1 * this.dx2);
            a17 = (this.dx1 * this.dy3 - this.dy1 * this.dx3) / (this.dx1 * this.dy2 - this.dy1 * this.dx2);
            a11 = x1 - x0 + a18 * x1;
            a12 = x3 - x0 + a17 * x3;
            a13 = x0;
            a14 = y1 - y0 + a18 * y1;
            a15 = y3 - y0 + a17 * y3;
            a16 = y0;
        }
        this.A = a15 - a16 * a17;
        this.B = a13 * a17 - a12;
        this.C = a12 * a16 - a13 * a15;
        this.D = a16 * a18 - a14;
        this.E = a11 - a13 * a18;
        this.F = a13 * a14 - a11 * a16;
        this.G = a14 * a17 - a15 * a18;
        this.H = a12 * a18 - a11 * a17;
        this.I = a11 * a15 - a12 * a14;
    }
    
    @Override
    protected void transformSpace(final Rectangle rect) {
        rect.x = (int)Math.min(Math.min(this.x0, this.x1), Math.min(this.x2, this.x3));
        rect.y = (int)Math.min(Math.min(this.y0, this.y1), Math.min(this.y2, this.y3));
        rect.width = (int)Math.max(Math.max(this.x0, this.x1), Math.max(this.x2, this.x3)) - rect.x;
        rect.height = (int)Math.max(Math.max(this.y0, this.y1), Math.max(this.y2, this.y3)) - rect.y;
    }
    
    public float getOriginX() {
        return this.x0 - (int)Math.min(Math.min(this.x0, this.x1), Math.min(this.x2, this.x3));
    }
    
    public float getOriginY() {
        return this.y0 - (int)Math.min(Math.min(this.y0, this.y1), Math.min(this.y2, this.y3));
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        out[0] = this.originalSpace.width * (this.A * x + this.B * y + this.C) / (this.G * x + this.H * y + this.I);
        out[1] = this.originalSpace.height * (this.D * x + this.E * y + this.F) / (this.G * x + this.H * y + this.I);
    }
    
    @Override
    public String toString() {
        return "Distort/Perspective...";
    }
}
