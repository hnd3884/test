package org.apache.commons.math3.geometry.euclidean.oned;

import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;

public class OrientedPoint implements Hyperplane<Euclidean1D>
{
    private static final double DEFAULT_TOLERANCE = 1.0E-10;
    private Vector1D location;
    private boolean direct;
    private final double tolerance;
    
    public OrientedPoint(final Vector1D location, final boolean direct, final double tolerance) {
        this.location = location;
        this.direct = direct;
        this.tolerance = tolerance;
    }
    
    @Deprecated
    public OrientedPoint(final Vector1D location, final boolean direct) {
        this(location, direct, 1.0E-10);
    }
    
    public OrientedPoint copySelf() {
        return this;
    }
    
    public double getOffset(final Vector<Euclidean1D> vector) {
        return this.getOffset((Point<Euclidean1D>)vector);
    }
    
    public double getOffset(final Point<Euclidean1D> point) {
        final double delta = ((Vector1D)point).getX() - this.location.getX();
        return this.direct ? delta : (-delta);
    }
    
    public SubOrientedPoint wholeHyperplane() {
        return new SubOrientedPoint(this, null);
    }
    
    public IntervalsSet wholeSpace() {
        return new IntervalsSet(this.tolerance);
    }
    
    public boolean sameOrientationAs(final Hyperplane<Euclidean1D> other) {
        return !(this.direct ^ ((OrientedPoint)other).direct);
    }
    
    public Point<Euclidean1D> project(final Point<Euclidean1D> point) {
        return this.location;
    }
    
    public double getTolerance() {
        return this.tolerance;
    }
    
    public Vector1D getLocation() {
        return this.location;
    }
    
    public boolean isDirect() {
        return this.direct;
    }
    
    public void revertSelf() {
        this.direct = !this.direct;
    }
}
