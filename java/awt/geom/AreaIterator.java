package java.awt.geom;

import java.util.NoSuchElementException;
import sun.awt.geom.Curve;
import java.util.Vector;

class AreaIterator implements PathIterator
{
    private AffineTransform transform;
    private Vector curves;
    private int index;
    private Curve prevcurve;
    private Curve thiscurve;
    
    public AreaIterator(final Vector curves, final AffineTransform transform) {
        this.curves = curves;
        this.transform = transform;
        if (curves.size() >= 1) {
            this.thiscurve = curves.get(0);
        }
    }
    
    @Override
    public int getWindingRule() {
        return 1;
    }
    
    @Override
    public boolean isDone() {
        return this.prevcurve == null && this.thiscurve == null;
    }
    
    @Override
    public void next() {
        if (this.prevcurve != null) {
            this.prevcurve = null;
        }
        else {
            this.prevcurve = this.thiscurve;
            ++this.index;
            if (this.index < this.curves.size()) {
                this.thiscurve = this.curves.get(this.index);
                if (this.thiscurve.getOrder() != 0 && this.prevcurve.getX1() == this.thiscurve.getX0() && this.prevcurve.getY1() == this.thiscurve.getY0()) {
                    this.prevcurve = null;
                }
            }
            else {
                this.thiscurve = null;
            }
        }
    }
    
    @Override
    public int currentSegment(final float[] array) {
        final double[] array2 = new double[6];
        final int currentSegment = this.currentSegment(array2);
        for (int n = (currentSegment == 4) ? 0 : ((currentSegment == 2) ? 2 : ((currentSegment == 3) ? 3 : 1)), i = 0; i < n * 2; ++i) {
            array[i] = (float)array2[i];
        }
        return currentSegment;
    }
    
    @Override
    public int currentSegment(final double[] array) {
        int segment;
        int order;
        if (this.prevcurve != null) {
            if (this.thiscurve == null || this.thiscurve.getOrder() == 0) {
                return 4;
            }
            array[0] = this.thiscurve.getX0();
            array[1] = this.thiscurve.getY0();
            segment = 1;
            order = 1;
        }
        else {
            if (this.thiscurve == null) {
                throw new NoSuchElementException("area iterator out of bounds");
            }
            segment = this.thiscurve.getSegment(array);
            order = this.thiscurve.getOrder();
            if (order == 0) {
                order = 1;
            }
        }
        if (this.transform != null) {
            this.transform.transform(array, 0, array, 0, order);
        }
        return segment;
    }
}
