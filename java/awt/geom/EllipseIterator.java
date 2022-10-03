package java.awt.geom;

import java.util.NoSuchElementException;

class EllipseIterator implements PathIterator
{
    double x;
    double y;
    double w;
    double h;
    AffineTransform affine;
    int index;
    public static final double CtrlVal = 0.5522847498307933;
    private static final double pcv = 0.7761423749153966;
    private static final double ncv = 0.22385762508460333;
    private static double[][] ctrlpts;
    
    EllipseIterator(final Ellipse2D ellipse2D, final AffineTransform affine) {
        this.x = ellipse2D.getX();
        this.y = ellipse2D.getY();
        this.w = ellipse2D.getWidth();
        this.h = ellipse2D.getHeight();
        this.affine = affine;
        if (this.w < 0.0 || this.h < 0.0) {
            this.index = 6;
        }
    }
    
    @Override
    public int getWindingRule() {
        return 1;
    }
    
    @Override
    public boolean isDone() {
        return this.index > 5;
    }
    
    @Override
    public void next() {
        ++this.index;
    }
    
    @Override
    public int currentSegment(final float[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("ellipse iterator out of bounds");
        }
        if (this.index == 5) {
            return 4;
        }
        if (this.index == 0) {
            final double[] array2 = EllipseIterator.ctrlpts[3];
            array[0] = (float)(this.x + array2[4] * this.w);
            array[1] = (float)(this.y + array2[5] * this.h);
            if (this.affine != null) {
                this.affine.transform(array, 0, array, 0, 1);
            }
            return 0;
        }
        final double[] array3 = EllipseIterator.ctrlpts[this.index - 1];
        array[0] = (float)(this.x + array3[0] * this.w);
        array[1] = (float)(this.y + array3[1] * this.h);
        array[2] = (float)(this.x + array3[2] * this.w);
        array[3] = (float)(this.y + array3[3] * this.h);
        array[4] = (float)(this.x + array3[4] * this.w);
        array[5] = (float)(this.y + array3[5] * this.h);
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, 3);
        }
        return 3;
    }
    
    @Override
    public int currentSegment(final double[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("ellipse iterator out of bounds");
        }
        if (this.index == 5) {
            return 4;
        }
        if (this.index == 0) {
            final double[] array2 = EllipseIterator.ctrlpts[3];
            array[0] = this.x + array2[4] * this.w;
            array[1] = this.y + array2[5] * this.h;
            if (this.affine != null) {
                this.affine.transform(array, 0, array, 0, 1);
            }
            return 0;
        }
        final double[] array3 = EllipseIterator.ctrlpts[this.index - 1];
        array[0] = this.x + array3[0] * this.w;
        array[1] = this.y + array3[1] * this.h;
        array[2] = this.x + array3[2] * this.w;
        array[3] = this.y + array3[3] * this.h;
        array[4] = this.x + array3[4] * this.w;
        array[5] = this.y + array3[5] * this.h;
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, 3);
        }
        return 3;
    }
    
    static {
        EllipseIterator.ctrlpts = new double[][] { { 1.0, 0.7761423749153966, 0.7761423749153966, 1.0, 0.5, 1.0 }, { 0.22385762508460333, 1.0, 0.0, 0.7761423749153966, 0.0, 0.5 }, { 0.0, 0.22385762508460333, 0.22385762508460333, 0.0, 0.5, 0.0 }, { 0.7761423749153966, 0.0, 1.0, 0.22385762508460333, 1.0, 0.5 } };
    }
}
