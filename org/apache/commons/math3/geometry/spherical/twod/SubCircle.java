package org.apache.commons.math3.geometry.spherical.twod;

import org.apache.commons.math3.geometry.spherical.oned.Arc;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;

public class SubCircle extends AbstractSubHyperplane<Sphere2D, Sphere1D>
{
    public SubCircle(final Hyperplane<Sphere2D> hyperplane, final Region<Sphere1D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }
    
    @Override
    protected AbstractSubHyperplane<Sphere2D, Sphere1D> buildNew(final Hyperplane<Sphere2D> hyperplane, final Region<Sphere1D> remainingRegion) {
        return new SubCircle(hyperplane, remainingRegion);
    }
    
    @Override
    public SubHyperplane.SplitSubHyperplane<Sphere2D> split(final Hyperplane<Sphere2D> hyperplane) {
        final Circle thisCircle = (Circle)this.getHyperplane();
        final Circle otherCircle = (Circle)hyperplane;
        final double angle = Vector3D.angle(thisCircle.getPole(), otherCircle.getPole());
        if (angle < thisCircle.getTolerance() || angle > 3.141592653589793 - thisCircle.getTolerance()) {
            return new SubHyperplane.SplitSubHyperplane<Sphere2D>(null, null);
        }
        final Arc arc = thisCircle.getInsideArc(otherCircle);
        final ArcsSet.Split split = ((ArcsSet)this.getRemainingRegion()).split(arc);
        final ArcsSet plus = split.getPlus();
        final ArcsSet minus = split.getMinus();
        return new SubHyperplane.SplitSubHyperplane<Sphere2D>((plus == null) ? null : new SubCircle(thisCircle.copySelf(), plus), (minus == null) ? null : new SubCircle(thisCircle.copySelf(), minus));
    }
}
