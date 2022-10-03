package org.apache.commons.math3.geometry.euclidean.oned;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;

public class SubOrientedPoint extends AbstractSubHyperplane<Euclidean1D, Euclidean1D>
{
    public SubOrientedPoint(final Hyperplane<Euclidean1D> hyperplane, final Region<Euclidean1D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }
    
    @Override
    public double getSize() {
        return 0.0;
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    protected AbstractSubHyperplane<Euclidean1D, Euclidean1D> buildNew(final Hyperplane<Euclidean1D> hyperplane, final Region<Euclidean1D> remainingRegion) {
        return new SubOrientedPoint(hyperplane, remainingRegion);
    }
    
    @Override
    public SubHyperplane.SplitSubHyperplane<Euclidean1D> split(final Hyperplane<Euclidean1D> hyperplane) {
        final double global = hyperplane.getOffset(((OrientedPoint)this.getHyperplane()).getLocation());
        if (global < -1.0E-10) {
            return new SubHyperplane.SplitSubHyperplane<Euclidean1D>(null, this);
        }
        if (global > 1.0E-10) {
            return new SubHyperplane.SplitSubHyperplane<Euclidean1D>(this, null);
        }
        return new SubHyperplane.SplitSubHyperplane<Euclidean1D>(null, null);
    }
}
