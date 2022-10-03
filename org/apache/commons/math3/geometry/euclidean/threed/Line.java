package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.partitioning.Embedding;

public class Line implements Embedding<Euclidean3D, Euclidean1D>
{
    private static final double DEFAULT_TOLERANCE = 1.0E-10;
    private Vector3D direction;
    private Vector3D zero;
    private final double tolerance;
    
    public Line(final Vector3D p1, final Vector3D p2, final double tolerance) throws MathIllegalArgumentException {
        this.reset(p1, p2);
        this.tolerance = tolerance;
    }
    
    public Line(final Line line) {
        this.direction = line.direction;
        this.zero = line.zero;
        this.tolerance = line.tolerance;
    }
    
    @Deprecated
    public Line(final Vector3D p1, final Vector3D p2) throws MathIllegalArgumentException {
        this(p1, p2, 1.0E-10);
    }
    
    public void reset(final Vector3D p1, final Vector3D p2) throws MathIllegalArgumentException {
        final Vector3D delta = p2.subtract((Vector<Euclidean3D>)p1);
        final double norm2 = delta.getNormSq();
        if (norm2 == 0.0) {
            throw new MathIllegalArgumentException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        this.direction = new Vector3D(1.0 / FastMath.sqrt(norm2), delta);
        this.zero = new Vector3D(1.0, p1, -p1.dotProduct(delta) / norm2, delta);
    }
    
    public double getTolerance() {
        return this.tolerance;
    }
    
    public Line revert() {
        final Line reverted = new Line(this);
        reverted.direction = reverted.direction.negate();
        return reverted;
    }
    
    public Vector3D getDirection() {
        return this.direction;
    }
    
    public Vector3D getOrigin() {
        return this.zero;
    }
    
    public double getAbscissa(final Vector3D point) {
        return point.subtract((Vector<Euclidean3D>)this.zero).dotProduct(this.direction);
    }
    
    public Vector3D pointAt(final double abscissa) {
        return new Vector3D(1.0, this.zero, abscissa, this.direction);
    }
    
    public Vector1D toSubSpace(final Vector<Euclidean3D> vector) {
        return this.toSubSpace((Point<Euclidean3D>)vector);
    }
    
    public Vector3D toSpace(final Vector<Euclidean1D> vector) {
        return this.toSpace((Point<Euclidean1D>)vector);
    }
    
    public Vector1D toSubSpace(final Point<Euclidean3D> point) {
        return new Vector1D(this.getAbscissa((Vector3D)point));
    }
    
    public Vector3D toSpace(final Point<Euclidean1D> point) {
        return this.pointAt(((Vector1D)point).getX());
    }
    
    public boolean isSimilarTo(final Line line) {
        final double angle = Vector3D.angle(this.direction, line.direction);
        return (angle < this.tolerance || angle > 3.141592653589793 - this.tolerance) && this.contains(line.zero);
    }
    
    public boolean contains(final Vector3D p) {
        return this.distance(p) < this.tolerance;
    }
    
    public double distance(final Vector3D p) {
        final Vector3D d = p.subtract((Vector<Euclidean3D>)this.zero);
        final Vector3D n = new Vector3D(1.0, d, -d.dotProduct(this.direction), this.direction);
        return n.getNorm();
    }
    
    public double distance(final Line line) {
        final Vector3D normal = Vector3D.crossProduct(this.direction, line.direction);
        final double n = normal.getNorm();
        if (n < Precision.SAFE_MIN) {
            return this.distance(line.zero);
        }
        final double offset = line.zero.subtract((Vector<Euclidean3D>)this.zero).dotProduct(normal) / n;
        return FastMath.abs(offset);
    }
    
    public Vector3D closestPoint(final Line line) {
        final double cos = this.direction.dotProduct(line.direction);
        final double n = 1.0 - cos * cos;
        if (n < Precision.EPSILON) {
            return this.zero;
        }
        final Vector3D delta0 = line.zero.subtract((Vector<Euclidean3D>)this.zero);
        final double a = delta0.dotProduct(this.direction);
        final double b = delta0.dotProduct(line.direction);
        return new Vector3D(1.0, this.zero, (a - b * cos) / n, this.direction);
    }
    
    public Vector3D intersection(final Line line) {
        final Vector3D closest = this.closestPoint(line);
        return line.contains(closest) ? closest : null;
    }
    
    public SubLine wholeLine() {
        return new SubLine(this, new IntervalsSet(this.tolerance));
    }
}
