package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;

public class SubLimitAngle extends AbstractSubHyperplane<Sphere1D, Sphere1D>
{
    public SubLimitAngle(final Hyperplane<Sphere1D> hyperplane, final Region<Sphere1D> remainingRegion) {
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
    protected AbstractSubHyperplane<Sphere1D, Sphere1D> buildNew(final Hyperplane<Sphere1D> hyperplane, final Region<Sphere1D> remainingRegion) {
        return new SubLimitAngle(hyperplane, remainingRegion);
    }
    
    @Override
    public SubHyperplane.SplitSubHyperplane<Sphere1D> split(final Hyperplane<Sphere1D> hyperplane) {
        final double global = hyperplane.getOffset(((LimitAngle)this.getHyperplane()).getLocation());
        return (global < -1.0E-10) ? new SubHyperplane.SplitSubHyperplane<Sphere1D>(null, this) : new SubHyperplane.SplitSubHyperplane<Sphere1D>(this, null);
    }
}
