package java.awt.geom;

import java.util.NoSuchElementException;

class CubicIterator implements PathIterator
{
    CubicCurve2D cubic;
    AffineTransform affine;
    int index;
    
    CubicIterator(final CubicCurve2D cubic, final AffineTransform affine) {
        this.cubic = cubic;
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
            throw new NoSuchElementException("cubic iterator iterator out of bounds");
        }
        int n;
        if (this.index == 0) {
            array[0] = (float)this.cubic.getX1();
            array[1] = (float)this.cubic.getY1();
            n = 0;
        }
        else {
            array[0] = (float)this.cubic.getCtrlX1();
            array[1] = (float)this.cubic.getCtrlY1();
            array[2] = (float)this.cubic.getCtrlX2();
            array[3] = (float)this.cubic.getCtrlY2();
            array[4] = (float)this.cubic.getX2();
            array[5] = (float)this.cubic.getY2();
            n = 3;
        }
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, (this.index == 0) ? 1 : 3);
        }
        return n;
    }
    
    @Override
    public int currentSegment(final double[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("cubic iterator iterator out of bounds");
        }
        int n;
        if (this.index == 0) {
            array[0] = this.cubic.getX1();
            array[1] = this.cubic.getY1();
            n = 0;
        }
        else {
            array[0] = this.cubic.getCtrlX1();
            array[1] = this.cubic.getCtrlY1();
            array[2] = this.cubic.getCtrlX2();
            array[3] = this.cubic.getCtrlY2();
            array[4] = this.cubic.getX2();
            array[5] = this.cubic.getY2();
            n = 3;
        }
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, (this.index == 0) ? 1 : 3);
        }
        return n;
    }
}
