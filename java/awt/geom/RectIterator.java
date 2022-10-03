package java.awt.geom;

import java.util.NoSuchElementException;

class RectIterator implements PathIterator
{
    double x;
    double y;
    double w;
    double h;
    AffineTransform affine;
    int index;
    
    RectIterator(final Rectangle2D rectangle2D, final AffineTransform affine) {
        this.x = rectangle2D.getX();
        this.y = rectangle2D.getY();
        this.w = rectangle2D.getWidth();
        this.h = rectangle2D.getHeight();
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
            throw new NoSuchElementException("rect iterator out of bounds");
        }
        if (this.index == 5) {
            return 4;
        }
        array[0] = (float)this.x;
        array[1] = (float)this.y;
        if (this.index == 1 || this.index == 2) {
            final int n = 0;
            array[n] += (float)this.w;
        }
        if (this.index == 2 || this.index == 3) {
            final int n2 = 1;
            array[n2] += (float)this.h;
        }
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, 1);
        }
        return (this.index != 0) ? 1 : 0;
    }
    
    @Override
    public int currentSegment(final double[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("rect iterator out of bounds");
        }
        if (this.index == 5) {
            return 4;
        }
        array[0] = this.x;
        array[1] = this.y;
        if (this.index == 1 || this.index == 2) {
            final int n = 0;
            array[n] += this.w;
        }
        if (this.index == 2 || this.index == 3) {
            final int n2 = 1;
            array[n2] += this.h;
        }
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, 1);
        }
        return (this.index != 0) ? 1 : 0;
    }
}
