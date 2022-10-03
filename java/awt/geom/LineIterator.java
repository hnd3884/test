package java.awt.geom;

import java.util.NoSuchElementException;

class LineIterator implements PathIterator
{
    Line2D line;
    AffineTransform affine;
    int index;
    
    LineIterator(final Line2D line, final AffineTransform affine) {
        this.line = line;
        this.affine = affine;
    }
    
    @Override
    public int getWindingRule() {
        return 1;
    }
    
    @Override
    public boolean isDone() {
        return this.index > 1;
    }
    
    @Override
    public void next() {
        ++this.index;
    }
    
    @Override
    public int currentSegment(final float[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("line iterator out of bounds");
        }
        int n;
        if (this.index == 0) {
            array[0] = (float)this.line.getX1();
            array[1] = (float)this.line.getY1();
            n = 0;
        }
        else {
            array[0] = (float)this.line.getX2();
            array[1] = (float)this.line.getY2();
            n = 1;
        }
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, 1);
        }
        return n;
    }
    
    @Override
    public int currentSegment(final double[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("line iterator out of bounds");
        }
        int n;
        if (this.index == 0) {
            array[0] = this.line.getX1();
            array[1] = this.line.getY1();
            n = 0;
        }
        else {
            array[0] = this.line.getX2();
            array[1] = this.line.getY2();
            n = 1;
        }
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, 1);
        }
        return n;
    }
}
