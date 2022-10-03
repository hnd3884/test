package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.geometry.euclidean.oned.OrientedPoint;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.partitioning.Transform;
import java.awt.geom.AffineTransform;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.partitioning.Embedding;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;

public class Line implements Hyperplane<Euclidean2D>, Embedding<Euclidean2D, Euclidean1D>
{
    private static final double DEFAULT_TOLERANCE = 1.0E-10;
    private double angle;
    private double cos;
    private double sin;
    private double originOffset;
    private final double tolerance;
    private Line reverse;
    
    public Line(final Vector2D p1, final Vector2D p2, final double tolerance) {
        this.reset(p1, p2);
        this.tolerance = tolerance;
    }
    
    public Line(final Vector2D p, final double angle, final double tolerance) {
        this.reset(p, angle);
        this.tolerance = tolerance;
    }
    
    private Line(final double angle, final double cos, final double sin, final double originOffset, final double tolerance) {
        this.angle = angle;
        this.cos = cos;
        this.sin = sin;
        this.originOffset = originOffset;
        this.tolerance = tolerance;
        this.reverse = null;
    }
    
    @Deprecated
    public Line(final Vector2D p1, final Vector2D p2) {
        this(p1, p2, 1.0E-10);
    }
    
    @Deprecated
    public Line(final Vector2D p, final double angle) {
        this(p, angle, 1.0E-10);
    }
    
    public Line(final Line line) {
        this.angle = MathUtils.normalizeAngle(line.angle, 3.141592653589793);
        this.cos = line.cos;
        this.sin = line.sin;
        this.originOffset = line.originOffset;
        this.tolerance = line.tolerance;
        this.reverse = null;
    }
    
    public Line copySelf() {
        return new Line(this);
    }
    
    public void reset(final Vector2D p1, final Vector2D p2) {
        this.unlinkReverse();
        final double dx = p2.getX() - p1.getX();
        final double dy = p2.getY() - p1.getY();
        final double d = FastMath.hypot(dx, dy);
        if (d == 0.0) {
            this.angle = 0.0;
            this.cos = 1.0;
            this.sin = 0.0;
            this.originOffset = p1.getY();
        }
        else {
            this.angle = 3.141592653589793 + FastMath.atan2(-dy, -dx);
            this.cos = dx / d;
            this.sin = dy / d;
            this.originOffset = MathArrays.linearCombination(p2.getX(), p1.getY(), -p1.getX(), p2.getY()) / d;
        }
    }
    
    public void reset(final Vector2D p, final double alpha) {
        this.unlinkReverse();
        this.angle = MathUtils.normalizeAngle(alpha, 3.141592653589793);
        this.cos = FastMath.cos(this.angle);
        this.sin = FastMath.sin(this.angle);
        this.originOffset = MathArrays.linearCombination(this.cos, p.getY(), -this.sin, p.getX());
    }
    
    public void revertSelf() {
        this.unlinkReverse();
        if (this.angle < 3.141592653589793) {
            this.angle += 3.141592653589793;
        }
        else {
            this.angle -= 3.141592653589793;
        }
        this.cos = -this.cos;
        this.sin = -this.sin;
        this.originOffset = -this.originOffset;
    }
    
    private void unlinkReverse() {
        if (this.reverse != null) {
            this.reverse.reverse = null;
        }
        this.reverse = null;
    }
    
    public Line getReverse() {
        if (this.reverse == null) {
            this.reverse = new Line((this.angle < 3.141592653589793) ? (this.angle + 3.141592653589793) : (this.angle - 3.141592653589793), -this.cos, -this.sin, -this.originOffset, this.tolerance);
            this.reverse.reverse = this;
        }
        return this.reverse;
    }
    
    public Vector1D toSubSpace(final Vector<Euclidean2D> vector) {
        return this.toSubSpace((Point<Euclidean2D>)vector);
    }
    
    public Vector2D toSpace(final Vector<Euclidean1D> vector) {
        return this.toSpace((Point<Euclidean1D>)vector);
    }
    
    public Vector1D toSubSpace(final Point<Euclidean2D> point) {
        final Vector2D p2 = (Vector2D)point;
        return new Vector1D(MathArrays.linearCombination(this.cos, p2.getX(), this.sin, p2.getY()));
    }
    
    public Vector2D toSpace(final Point<Euclidean1D> point) {
        final double abscissa = ((Vector1D)point).getX();
        return new Vector2D(MathArrays.linearCombination(abscissa, this.cos, -this.originOffset, this.sin), MathArrays.linearCombination(abscissa, this.sin, this.originOffset, this.cos));
    }
    
    public Vector2D intersection(final Line other) {
        final double d = MathArrays.linearCombination(this.sin, other.cos, -other.sin, this.cos);
        if (FastMath.abs(d) < this.tolerance) {
            return null;
        }
        return new Vector2D(MathArrays.linearCombination(this.cos, other.originOffset, -other.cos, this.originOffset) / d, MathArrays.linearCombination(this.sin, other.originOffset, -other.sin, this.originOffset) / d);
    }
    
    public Point<Euclidean2D> project(final Point<Euclidean2D> point) {
        return this.toSpace(this.toSubSpace(point));
    }
    
    public double getTolerance() {
        return this.tolerance;
    }
    
    public SubLine wholeHyperplane() {
        return new SubLine(this, new IntervalsSet(this.tolerance));
    }
    
    public PolygonsSet wholeSpace() {
        return new PolygonsSet(this.tolerance);
    }
    
    public double getOffset(final Line line) {
        return this.originOffset + ((MathArrays.linearCombination(this.cos, line.cos, this.sin, line.sin) > 0.0) ? (-line.originOffset) : line.originOffset);
    }
    
    public double getOffset(final Vector<Euclidean2D> vector) {
        return this.getOffset((Point<Euclidean2D>)vector);
    }
    
    public double getOffset(final Point<Euclidean2D> point) {
        final Vector2D p2 = (Vector2D)point;
        return MathArrays.linearCombination(this.sin, p2.getX(), -this.cos, p2.getY(), 1.0, this.originOffset);
    }
    
    public boolean sameOrientationAs(final Hyperplane<Euclidean2D> other) {
        final Line otherL = (Line)other;
        return MathArrays.linearCombination(this.sin, otherL.sin, this.cos, otherL.cos) >= 0.0;
    }
    
    public Vector2D getPointAt(final Vector1D abscissa, final double offset) {
        final double x = abscissa.getX();
        final double dOffset = offset - this.originOffset;
        return new Vector2D(MathArrays.linearCombination(x, this.cos, dOffset, this.sin), MathArrays.linearCombination(x, this.sin, -dOffset, this.cos));
    }
    
    public boolean contains(final Vector2D p) {
        return FastMath.abs(this.getOffset(p)) < this.tolerance;
    }
    
    public double distance(final Vector2D p) {
        return FastMath.abs(this.getOffset(p));
    }
    
    public boolean isParallelTo(final Line line) {
        return FastMath.abs(MathArrays.linearCombination(this.sin, line.cos, -this.cos, line.sin)) < this.tolerance;
    }
    
    public void translateToPoint(final Vector2D p) {
        this.originOffset = MathArrays.linearCombination(this.cos, p.getY(), -this.sin, p.getX());
    }
    
    public double getAngle() {
        return MathUtils.normalizeAngle(this.angle, 3.141592653589793);
    }
    
    public void setAngle(final double angle) {
        this.unlinkReverse();
        this.angle = MathUtils.normalizeAngle(angle, 3.141592653589793);
        this.cos = FastMath.cos(this.angle);
        this.sin = FastMath.sin(this.angle);
    }
    
    public double getOriginOffset() {
        return this.originOffset;
    }
    
    public void setOriginOffset(final double offset) {
        this.unlinkReverse();
        this.originOffset = offset;
    }
    
    @Deprecated
    public static Transform<Euclidean2D, Euclidean1D> getTransform(final AffineTransform transform) throws MathIllegalArgumentException {
        final double[] m = new double[6];
        transform.getMatrix(m);
        return new LineTransform(m[0], m[1], m[2], m[3], m[4], m[5]);
    }
    
    public static Transform<Euclidean2D, Euclidean1D> getTransform(final double cXX, final double cYX, final double cXY, final double cYY, final double cX1, final double cY1) throws MathIllegalArgumentException {
        return new LineTransform(cXX, cYX, cXY, cYY, cX1, cY1);
    }
    
    private static class LineTransform implements Transform<Euclidean2D, Euclidean1D>
    {
        private double cXX;
        private double cYX;
        private double cXY;
        private double cYY;
        private double cX1;
        private double cY1;
        private double c1Y;
        private double c1X;
        private double c11;
        
        LineTransform(final double cXX, final double cYX, final double cXY, final double cYY, final double cX1, final double cY1) throws MathIllegalArgumentException {
            this.cXX = cXX;
            this.cYX = cYX;
            this.cXY = cXY;
            this.cYY = cYY;
            this.cX1 = cX1;
            this.cY1 = cY1;
            this.c1Y = MathArrays.linearCombination(cXY, cY1, -cYY, cX1);
            this.c1X = MathArrays.linearCombination(cXX, cY1, -cYX, cX1);
            this.c11 = MathArrays.linearCombination(cXX, cYY, -cYX, cXY);
            if (FastMath.abs(this.c11) < 1.0E-20) {
                throw new MathIllegalArgumentException(LocalizedFormats.NON_INVERTIBLE_TRANSFORM, new Object[0]);
            }
        }
        
        public Vector2D apply(final Point<Euclidean2D> point) {
            final Vector2D p2D = (Vector2D)point;
            final double x = p2D.getX();
            final double y = p2D.getY();
            return new Vector2D(MathArrays.linearCombination(this.cXX, x, this.cXY, y, this.cX1, 1.0), MathArrays.linearCombination(this.cYX, x, this.cYY, y, this.cY1, 1.0));
        }
        
        public Line apply(final Hyperplane<Euclidean2D> hyperplane) {
            final Line line = (Line)hyperplane;
            final double rOffset = MathArrays.linearCombination(this.c1X, line.cos, this.c1Y, line.sin, this.c11, line.originOffset);
            final double rCos = MathArrays.linearCombination(this.cXX, line.cos, this.cXY, line.sin);
            final double rSin = MathArrays.linearCombination(this.cYX, line.cos, this.cYY, line.sin);
            final double inv = 1.0 / FastMath.sqrt(rSin * rSin + rCos * rCos);
            return new Line(3.141592653589793 + FastMath.atan2(-rSin, -rCos), inv * rCos, inv * rSin, inv * rOffset, line.tolerance, null);
        }
        
        public SubHyperplane<Euclidean1D> apply(final SubHyperplane<Euclidean1D> sub, final Hyperplane<Euclidean2D> original, final Hyperplane<Euclidean2D> transformed) {
            final OrientedPoint op = (OrientedPoint)sub.getHyperplane();
            final Line originalLine = (Line)original;
            final Line transformedLine = (Line)transformed;
            final Vector1D newLoc = transformedLine.toSubSpace(this.apply((Point<Euclidean2D>)originalLine.toSpace(op.getLocation())));
            return new OrientedPoint(newLoc, op.isDirect(), originalLine.tolerance).wholeHyperplane();
        }
    }
}
