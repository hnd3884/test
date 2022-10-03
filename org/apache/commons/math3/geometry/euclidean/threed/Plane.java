package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.partitioning.Embedding;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;

public class Plane implements Hyperplane<Euclidean3D>, Embedding<Euclidean3D, Euclidean2D>
{
    private static final double DEFAULT_TOLERANCE = 1.0E-10;
    private double originOffset;
    private Vector3D origin;
    private Vector3D u;
    private Vector3D v;
    private Vector3D w;
    private final double tolerance;
    
    public Plane(final Vector3D normal, final double tolerance) throws MathArithmeticException {
        this.setNormal(normal);
        this.tolerance = tolerance;
        this.originOffset = 0.0;
        this.setFrame();
    }
    
    public Plane(final Vector3D p, final Vector3D normal, final double tolerance) throws MathArithmeticException {
        this.setNormal(normal);
        this.tolerance = tolerance;
        this.originOffset = -p.dotProduct(this.w);
        this.setFrame();
    }
    
    public Plane(final Vector3D p1, final Vector3D p2, final Vector3D p3, final double tolerance) throws MathArithmeticException {
        this(p1, p2.subtract((Vector<Euclidean3D>)p1).crossProduct(p3.subtract((Vector<Euclidean3D>)p1)), tolerance);
    }
    
    @Deprecated
    public Plane(final Vector3D normal) throws MathArithmeticException {
        this(normal, 1.0E-10);
    }
    
    @Deprecated
    public Plane(final Vector3D p, final Vector3D normal) throws MathArithmeticException {
        this(p, normal, 1.0E-10);
    }
    
    @Deprecated
    public Plane(final Vector3D p1, final Vector3D p2, final Vector3D p3) throws MathArithmeticException {
        this(p1, p2, p3, 1.0E-10);
    }
    
    public Plane(final Plane plane) {
        this.originOffset = plane.originOffset;
        this.origin = plane.origin;
        this.u = plane.u;
        this.v = plane.v;
        this.w = plane.w;
        this.tolerance = plane.tolerance;
    }
    
    public Plane copySelf() {
        return new Plane(this);
    }
    
    public void reset(final Vector3D p, final Vector3D normal) throws MathArithmeticException {
        this.setNormal(normal);
        this.originOffset = -p.dotProduct(this.w);
        this.setFrame();
    }
    
    public void reset(final Plane original) {
        this.originOffset = original.originOffset;
        this.origin = original.origin;
        this.u = original.u;
        this.v = original.v;
        this.w = original.w;
    }
    
    private void setNormal(final Vector3D normal) throws MathArithmeticException {
        final double norm = normal.getNorm();
        if (norm < 1.0E-10) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        this.w = new Vector3D(1.0 / norm, normal);
    }
    
    private void setFrame() {
        this.origin = new Vector3D(-this.originOffset, this.w);
        this.u = this.w.orthogonal();
        this.v = Vector3D.crossProduct(this.w, this.u);
    }
    
    public Vector3D getOrigin() {
        return this.origin;
    }
    
    public Vector3D getNormal() {
        return this.w;
    }
    
    public Vector3D getU() {
        return this.u;
    }
    
    public Vector3D getV() {
        return this.v;
    }
    
    public Point<Euclidean3D> project(final Point<Euclidean3D> point) {
        return this.toSpace(this.toSubSpace(point));
    }
    
    public double getTolerance() {
        return this.tolerance;
    }
    
    public void revertSelf() {
        final Vector3D tmp = this.u;
        this.u = this.v;
        this.v = tmp;
        this.w = this.w.negate();
        this.originOffset = -this.originOffset;
    }
    
    public Vector2D toSubSpace(final Vector<Euclidean3D> vector) {
        return this.toSubSpace((Point<Euclidean3D>)vector);
    }
    
    public Vector3D toSpace(final Vector<Euclidean2D> vector) {
        return this.toSpace((Point<Euclidean2D>)vector);
    }
    
    public Vector2D toSubSpace(final Point<Euclidean3D> point) {
        final Vector3D p3D = (Vector3D)point;
        return new Vector2D(p3D.dotProduct(this.u), p3D.dotProduct(this.v));
    }
    
    public Vector3D toSpace(final Point<Euclidean2D> point) {
        final Vector2D p2D = (Vector2D)point;
        return new Vector3D(p2D.getX(), this.u, p2D.getY(), this.v, -this.originOffset, this.w);
    }
    
    public Vector3D getPointAt(final Vector2D inPlane, final double offset) {
        return new Vector3D(inPlane.getX(), this.u, inPlane.getY(), this.v, offset - this.originOffset, this.w);
    }
    
    public boolean isSimilarTo(final Plane plane) {
        final double angle = Vector3D.angle(this.w, plane.w);
        return (angle < 1.0E-10 && FastMath.abs(this.originOffset - plane.originOffset) < this.tolerance) || (angle > 3.141592653489793 && FastMath.abs(this.originOffset + plane.originOffset) < this.tolerance);
    }
    
    public Plane rotate(final Vector3D center, final Rotation rotation) {
        final Vector3D delta = this.origin.subtract((Vector<Euclidean3D>)center);
        final Plane plane = new Plane(center.add((Vector<Euclidean3D>)rotation.applyTo(delta)), rotation.applyTo(this.w), this.tolerance);
        plane.u = rotation.applyTo(this.u);
        plane.v = rotation.applyTo(this.v);
        return plane;
    }
    
    public Plane translate(final Vector3D translation) {
        final Plane plane = new Plane(this.origin.add((Vector<Euclidean3D>)translation), this.w, this.tolerance);
        plane.u = this.u;
        plane.v = this.v;
        return plane;
    }
    
    public Vector3D intersection(final Line line) {
        final Vector3D direction = line.getDirection();
        final double dot = this.w.dotProduct(direction);
        if (FastMath.abs(dot) < 1.0E-10) {
            return null;
        }
        final Vector3D point = line.toSpace((Point<Euclidean1D>)Vector1D.ZERO);
        final double k = -(this.originOffset + this.w.dotProduct(point)) / dot;
        return new Vector3D(1.0, point, k, direction);
    }
    
    public Line intersection(final Plane other) {
        final Vector3D direction = Vector3D.crossProduct(this.w, other.w);
        if (direction.getNorm() < this.tolerance) {
            return null;
        }
        final Vector3D point = intersection(this, other, new Plane(direction, this.tolerance));
        return new Line(point, point.add((Vector<Euclidean3D>)direction), this.tolerance);
    }
    
    public static Vector3D intersection(final Plane plane1, final Plane plane2, final Plane plane3) {
        final double a1 = plane1.w.getX();
        final double b1 = plane1.w.getY();
        final double c1 = plane1.w.getZ();
        final double d1 = plane1.originOffset;
        final double a2 = plane2.w.getX();
        final double b2 = plane2.w.getY();
        final double c2 = plane2.w.getZ();
        final double d2 = plane2.originOffset;
        final double a3 = plane3.w.getX();
        final double b3 = plane3.w.getY();
        final double c3 = plane3.w.getZ();
        final double d3 = plane3.originOffset;
        final double a4 = b2 * c3 - b3 * c2;
        final double b4 = c2 * a3 - c3 * a2;
        final double c4 = a2 * b3 - a3 * b2;
        final double determinant = a1 * a4 + b1 * b4 + c1 * c4;
        if (FastMath.abs(determinant) < 1.0E-10) {
            return null;
        }
        final double r = 1.0 / determinant;
        return new Vector3D((-a4 * d1 - (c1 * b3 - c3 * b1) * d2 - (c2 * b1 - c1 * b2) * d3) * r, (-b4 * d1 - (c3 * a1 - c1 * a3) * d2 - (c1 * a2 - c2 * a1) * d3) * r, (-c4 * d1 - (b1 * a3 - b3 * a1) * d2 - (b2 * a1 - b1 * a2) * d3) * r);
    }
    
    public SubPlane wholeHyperplane() {
        return new SubPlane(this, new PolygonsSet(this.tolerance));
    }
    
    public PolyhedronsSet wholeSpace() {
        return new PolyhedronsSet(this.tolerance);
    }
    
    public boolean contains(final Vector3D p) {
        return FastMath.abs(this.getOffset(p)) < this.tolerance;
    }
    
    public double getOffset(final Plane plane) {
        return this.originOffset + (this.sameOrientationAs(plane) ? (-plane.originOffset) : plane.originOffset);
    }
    
    public double getOffset(final Vector<Euclidean3D> vector) {
        return this.getOffset((Point<Euclidean3D>)vector);
    }
    
    public double getOffset(final Point<Euclidean3D> point) {
        return ((Vector3D)point).dotProduct(this.w) + this.originOffset;
    }
    
    public boolean sameOrientationAs(final Hyperplane<Euclidean3D> other) {
        return ((Plane)other).w.dotProduct(this.w) > 0.0;
    }
}
