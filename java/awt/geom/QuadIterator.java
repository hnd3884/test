package java.awt.geom;

import java.util.NoSuchElementException;

class QuadIterator implements PathIterator
{
    QuadCurve2D quad;
    AffineTransform affine;
    int index;
    
    QuadIterator(final QuadCurve2D quad, final AffineTransform affine) {
        this.quad = quad;
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
            throw new NoSuchElementException("quad iterator iterator out of bounds");
        }
        int n;
        if (this.index == 0) {
            array[0] = (float)this.quad.getX1();
            array[1] = (float)this.quad.getY1();
            n = 0;
        }
        else {
            array[0] = (float)this.quad.getCtrlX();
            array[1] = (float)this.quad.getCtrlY();
            array[2] = (float)this.quad.getX2();
            array[3] = (float)this.quad.getY2();
            n = 2;
        }
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, (this.index == 0) ? 1 : 2);
        }
        return n;
    }
    
    @Override
    public int currentSegment(final double[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("quad iterator iterator out of bounds");
        }
        int n;
        if (this.index == 0) {
            array[0] = this.quad.getX1();
            array[1] = this.quad.getY1();
            n = 0;
        }
        else {
            array[0] = this.quad.getCtrlX();
            array[1] = this.quad.getCtrlY();
            array[2] = this.quad.getX2();
            array[3] = this.quad.getY2();
            n = 2;
        }
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, (this.index == 0) ? 1 : 2);
        }
        return n;
    }
}
