package com.google.zxing.common;

public final class PerspectiveTransform
{
    private final float a11;
    private final float a12;
    private final float a13;
    private final float a21;
    private final float a22;
    private final float a23;
    private final float a31;
    private final float a32;
    private final float a33;
    
    private PerspectiveTransform(final float a11, final float a21, final float a31, final float a12, final float a22, final float a32, final float a13, final float a23, final float a33) {
        this.a11 = a11;
        this.a12 = a12;
        this.a13 = a13;
        this.a21 = a21;
        this.a22 = a22;
        this.a23 = a23;
        this.a31 = a31;
        this.a32 = a32;
        this.a33 = a33;
    }
    
    public static PerspectiveTransform quadrilateralToQuadrilateral(final float x0, final float y0, final float x1, final float y1, final float x2, final float y2, final float x3, final float y3, final float x0p, final float y0p, final float x1p, final float y1p, final float x2p, final float y2p, final float x3p, final float y3p) {
        final PerspectiveTransform qToS = quadrilateralToSquare(x0, y0, x1, y1, x2, y2, x3, y3);
        final PerspectiveTransform sToQ = squareToQuadrilateral(x0p, y0p, x1p, y1p, x2p, y2p, x3p, y3p);
        return sToQ.times(qToS);
    }
    
    public void transformPoints(final float[] points) {
        final int max = points.length;
        final float a11 = this.a11;
        final float a12 = this.a12;
        final float a13 = this.a13;
        final float a14 = this.a21;
        final float a15 = this.a22;
        final float a16 = this.a23;
        final float a17 = this.a31;
        final float a18 = this.a32;
        final float a19 = this.a33;
        for (int i = 0; i < max; i += 2) {
            final float x = points[i];
            final float y = points[i + 1];
            final float denominator = a13 * x + a16 * y + a19;
            points[i] = (a11 * x + a14 * y + a17) / denominator;
            points[i + 1] = (a12 * x + a15 * y + a18) / denominator;
        }
    }
    
    public void transformPoints(final float[] xValues, final float[] yValues) {
        for (int n = xValues.length, i = 0; i < n; ++i) {
            final float x = xValues[i];
            final float y = yValues[i];
            final float denominator = this.a13 * x + this.a23 * y + this.a33;
            xValues[i] = (this.a11 * x + this.a21 * y + this.a31) / denominator;
            yValues[i] = (this.a12 * x + this.a22 * y + this.a32) / denominator;
        }
    }
    
    public static PerspectiveTransform squareToQuadrilateral(final float x0, final float y0, final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        final float dy2 = y3 - y2;
        final float dy3 = y0 - y1 + y2 - y3;
        if (dy2 == 0.0f && dy3 == 0.0f) {
            return new PerspectiveTransform(x1 - x0, x2 - x1, x0, y1 - y0, y2 - y1, y0, 0.0f, 0.0f, 1.0f);
        }
        final float dx1 = x1 - x2;
        final float dx2 = x3 - x2;
        final float dx3 = x0 - x1 + x2 - x3;
        final float dy4 = y1 - y2;
        final float denominator = dx1 * dy2 - dx2 * dy4;
        final float a13 = (dx3 * dy2 - dx2 * dy3) / denominator;
        final float a14 = (dx1 * dy3 - dx3 * dy4) / denominator;
        return new PerspectiveTransform(x1 - x0 + a13 * x1, x3 - x0 + a14 * x3, x0, y1 - y0 + a13 * y1, y3 - y0 + a14 * y3, y0, a13, a14, 1.0f);
    }
    
    public static PerspectiveTransform quadrilateralToSquare(final float x0, final float y0, final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        return squareToQuadrilateral(x0, y0, x1, y1, x2, y2, x3, y3).buildAdjoint();
    }
    
    PerspectiveTransform buildAdjoint() {
        return new PerspectiveTransform(this.a22 * this.a33 - this.a23 * this.a32, this.a23 * this.a31 - this.a21 * this.a33, this.a21 * this.a32 - this.a22 * this.a31, this.a13 * this.a32 - this.a12 * this.a33, this.a11 * this.a33 - this.a13 * this.a31, this.a12 * this.a31 - this.a11 * this.a32, this.a12 * this.a23 - this.a13 * this.a22, this.a13 * this.a21 - this.a11 * this.a23, this.a11 * this.a22 - this.a12 * this.a21);
    }
    
    PerspectiveTransform times(final PerspectiveTransform other) {
        return new PerspectiveTransform(this.a11 * other.a11 + this.a21 * other.a12 + this.a31 * other.a13, this.a11 * other.a21 + this.a21 * other.a22 + this.a31 * other.a23, this.a11 * other.a31 + this.a21 * other.a32 + this.a31 * other.a33, this.a12 * other.a11 + this.a22 * other.a12 + this.a32 * other.a13, this.a12 * other.a21 + this.a22 * other.a22 + this.a32 * other.a23, this.a12 * other.a31 + this.a22 * other.a32 + this.a32 * other.a33, this.a13 * other.a11 + this.a23 * other.a12 + this.a33 * other.a13, this.a13 * other.a21 + this.a23 * other.a22 + this.a33 * other.a23, this.a13 * other.a31 + this.a23 * other.a32 + this.a33 * other.a33);
    }
}
