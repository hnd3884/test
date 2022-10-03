package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;

public class LimitAngle implements Hyperplane<Sphere1D>
{
    private S1Point location;
    private boolean direct;
    private final double tolerance;
    
    public LimitAngle(final S1Point location, final boolean direct, final double tolerance) {
        this.location = location;
        this.direct = direct;
        this.tolerance = tolerance;
    }
    
    public LimitAngle copySelf() {
        return this;
    }
    
    public double getOffset(final Point<Sphere1D> point) {
        final double delta = ((S1Point)point).getAlpha() - this.location.getAlpha();
        return this.direct ? delta : (-delta);
    }
    
    public boolean isDirect() {
        return this.direct;
    }
    
    public LimitAngle getReverse() {
        return new LimitAngle(this.location, !this.direct, this.tolerance);
    }
    
    public SubLimitAngle wholeHyperplane() {
        return new SubLimitAngle(this, null);
    }
    
    public ArcsSet wholeSpace() {
        return new ArcsSet(this.tolerance);
    }
    
    public boolean sameOrientationAs(final Hyperplane<Sphere1D> other) {
        return !(this.direct ^ ((LimitAngle)other).direct);
    }
    
    public S1Point getLocation() {
        return this.location;
    }
    
    public Point<Sphere1D> project(final Point<Sphere1D> point) {
        return this.location;
    }
    
    public double getTolerance() {
        return this.tolerance;
    }
}
