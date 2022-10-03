package org.apache.commons.math3.geometry.spherical.twod;

import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Transform;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.geometry.spherical.oned.Arc;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.spherical.oned.S1Point;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.geometry.partitioning.Embedding;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;

public class Circle implements Hyperplane<Sphere2D>, Embedding<Sphere2D, Sphere1D>
{
    private Vector3D pole;
    private Vector3D x;
    private Vector3D y;
    private final double tolerance;
    
    public Circle(final Vector3D pole, final double tolerance) {
        this.reset(pole);
        this.tolerance = tolerance;
    }
    
    public Circle(final S2Point first, final S2Point second, final double tolerance) {
        this.reset(first.getVector().crossProduct(second.getVector()));
        this.tolerance = tolerance;
    }
    
    private Circle(final Vector3D pole, final Vector3D x, final Vector3D y, final double tolerance) {
        this.pole = pole;
        this.x = x;
        this.y = y;
        this.tolerance = tolerance;
    }
    
    public Circle(final Circle circle) {
        this(circle.pole, circle.x, circle.y, circle.tolerance);
    }
    
    public Circle copySelf() {
        return new Circle(this);
    }
    
    public void reset(final Vector3D newPole) {
        this.pole = newPole.normalize();
        this.x = newPole.orthogonal();
        this.y = Vector3D.crossProduct(newPole, this.x).normalize();
    }
    
    public void revertSelf() {
        this.y = this.y.negate();
        this.pole = this.pole.negate();
    }
    
    public Circle getReverse() {
        return new Circle(this.pole.negate(), this.x, this.y.negate(), this.tolerance);
    }
    
    public Point<Sphere2D> project(final Point<Sphere2D> point) {
        return this.toSpace((Point<Sphere1D>)this.toSubSpace(point));
    }
    
    public double getTolerance() {
        return this.tolerance;
    }
    
    public S1Point toSubSpace(final Point<Sphere2D> point) {
        return new S1Point(this.getPhase(((S2Point)point).getVector()));
    }
    
    public double getPhase(final Vector3D direction) {
        return 3.141592653589793 + FastMath.atan2(-direction.dotProduct(this.y), -direction.dotProduct(this.x));
    }
    
    public S2Point toSpace(final Point<Sphere1D> point) {
        return new S2Point(this.getPointAt(((S1Point)point).getAlpha()));
    }
    
    public Vector3D getPointAt(final double alpha) {
        return new Vector3D(FastMath.cos(alpha), this.x, FastMath.sin(alpha), this.y);
    }
    
    public Vector3D getXAxis() {
        return this.x;
    }
    
    public Vector3D getYAxis() {
        return this.y;
    }
    
    public Vector3D getPole() {
        return this.pole;
    }
    
    public Arc getInsideArc(final Circle other) {
        final double alpha = this.getPhase(other.pole);
        final double halfPi = 1.5707963267948966;
        return new Arc(alpha - 1.5707963267948966, alpha + 1.5707963267948966, this.tolerance);
    }
    
    public SubCircle wholeHyperplane() {
        return new SubCircle(this, new ArcsSet(this.tolerance));
    }
    
    public SphericalPolygonsSet wholeSpace() {
        return new SphericalPolygonsSet(this.tolerance);
    }
    
    public double getOffset(final Point<Sphere2D> point) {
        return this.getOffset(((S2Point)point).getVector());
    }
    
    public double getOffset(final Vector3D direction) {
        return Vector3D.angle(this.pole, direction) - 1.5707963267948966;
    }
    
    public boolean sameOrientationAs(final Hyperplane<Sphere2D> other) {
        final Circle otherC = (Circle)other;
        return Vector3D.dotProduct(this.pole, otherC.pole) >= 0.0;
    }
    
    public static Transform<Sphere2D, Sphere1D> getTransform(final Rotation rotation) {
        return new CircleTransform(rotation);
    }
    
    private static class CircleTransform implements Transform<Sphere2D, Sphere1D>
    {
        private final Rotation rotation;
        
        CircleTransform(final Rotation rotation) {
            this.rotation = rotation;
        }
        
        public S2Point apply(final Point<Sphere2D> point) {
            return new S2Point(this.rotation.applyTo(((S2Point)point).getVector()));
        }
        
        public Circle apply(final Hyperplane<Sphere2D> hyperplane) {
            final Circle circle = (Circle)hyperplane;
            return new Circle(this.rotation.applyTo(circle.pole), this.rotation.applyTo(circle.x), this.rotation.applyTo(circle.y), circle.tolerance, null);
        }
        
        public SubHyperplane<Sphere1D> apply(final SubHyperplane<Sphere1D> sub, final Hyperplane<Sphere2D> original, final Hyperplane<Sphere2D> transformed) {
            return sub;
        }
    }
}
