package org.apache.commons.math3.geometry.euclidean.twod;

import java.text.NumberFormat;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.geometry.Vector;

public class Vector2D implements Vector<Euclidean2D>
{
    public static final Vector2D ZERO;
    public static final Vector2D NaN;
    public static final Vector2D POSITIVE_INFINITY;
    public static final Vector2D NEGATIVE_INFINITY;
    private static final long serialVersionUID = 266938651998679754L;
    private final double x;
    private final double y;
    
    public Vector2D(final double x, final double y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2D(final double[] v) throws DimensionMismatchException {
        if (v.length != 2) {
            throw new DimensionMismatchException(v.length, 2);
        }
        this.x = v[0];
        this.y = v[1];
    }
    
    public Vector2D(final double a, final Vector2D u) {
        this.x = a * u.x;
        this.y = a * u.y;
    }
    
    public Vector2D(final double a1, final Vector2D u1, final double a2, final Vector2D u2) {
        this.x = a1 * u1.x + a2 * u2.x;
        this.y = a1 * u1.y + a2 * u2.y;
    }
    
    public Vector2D(final double a1, final Vector2D u1, final double a2, final Vector2D u2, final double a3, final Vector2D u3) {
        this.x = a1 * u1.x + a2 * u2.x + a3 * u3.x;
        this.y = a1 * u1.y + a2 * u2.y + a3 * u3.y;
    }
    
    public Vector2D(final double a1, final Vector2D u1, final double a2, final Vector2D u2, final double a3, final Vector2D u3, final double a4, final Vector2D u4) {
        this.x = a1 * u1.x + a2 * u2.x + a3 * u3.x + a4 * u4.x;
        this.y = a1 * u1.y + a2 * u2.y + a3 * u3.y + a4 * u4.y;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double[] toArray() {
        return new double[] { this.x, this.y };
    }
    
    public Space getSpace() {
        return Euclidean2D.getInstance();
    }
    
    public Vector2D getZero() {
        return Vector2D.ZERO;
    }
    
    public double getNorm1() {
        return FastMath.abs(this.x) + FastMath.abs(this.y);
    }
    
    public double getNorm() {
        return FastMath.sqrt(this.x * this.x + this.y * this.y);
    }
    
    public double getNormSq() {
        return this.x * this.x + this.y * this.y;
    }
    
    public double getNormInf() {
        return FastMath.max(FastMath.abs(this.x), FastMath.abs(this.y));
    }
    
    public Vector2D add(final Vector<Euclidean2D> v) {
        final Vector2D v2 = (Vector2D)v;
        return new Vector2D(this.x + v2.getX(), this.y + v2.getY());
    }
    
    public Vector2D add(final double factor, final Vector<Euclidean2D> v) {
        final Vector2D v2 = (Vector2D)v;
        return new Vector2D(this.x + factor * v2.getX(), this.y + factor * v2.getY());
    }
    
    public Vector2D subtract(final Vector<Euclidean2D> p) {
        final Vector2D p2 = (Vector2D)p;
        return new Vector2D(this.x - p2.x, this.y - p2.y);
    }
    
    public Vector2D subtract(final double factor, final Vector<Euclidean2D> v) {
        final Vector2D v2 = (Vector2D)v;
        return new Vector2D(this.x - factor * v2.getX(), this.y - factor * v2.getY());
    }
    
    public Vector2D normalize() throws MathArithmeticException {
        final double s = this.getNorm();
        if (s == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR, new Object[0]);
        }
        return this.scalarMultiply(1.0 / s);
    }
    
    public static double angle(final Vector2D v1, final Vector2D v2) throws MathArithmeticException {
        final double normProduct = v1.getNorm() * v2.getNorm();
        if (normProduct == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        final double dot = v1.dotProduct(v2);
        final double threshold = normProduct * 0.9999;
        if (dot >= -threshold && dot <= threshold) {
            return FastMath.acos(dot / normProduct);
        }
        final double n = FastMath.abs(MathArrays.linearCombination(v1.x, v2.y, -v1.y, v2.x));
        if (dot >= 0.0) {
            return FastMath.asin(n / normProduct);
        }
        return 3.141592653589793 - FastMath.asin(n / normProduct);
    }
    
    public Vector2D negate() {
        return new Vector2D(-this.x, -this.y);
    }
    
    public Vector2D scalarMultiply(final double a) {
        return new Vector2D(a * this.x, a * this.y);
    }
    
    public boolean isNaN() {
        return Double.isNaN(this.x) || Double.isNaN(this.y);
    }
    
    public boolean isInfinite() {
        return !this.isNaN() && (Double.isInfinite(this.x) || Double.isInfinite(this.y));
    }
    
    public double distance1(final Vector<Euclidean2D> p) {
        final Vector2D p2 = (Vector2D)p;
        final double dx = FastMath.abs(p2.x - this.x);
        final double dy = FastMath.abs(p2.y - this.y);
        return dx + dy;
    }
    
    public double distance(final Vector<Euclidean2D> p) {
        return this.distance((Point<Euclidean2D>)p);
    }
    
    public double distance(final Point<Euclidean2D> p) {
        final Vector2D p2 = (Vector2D)p;
        final double dx = p2.x - this.x;
        final double dy = p2.y - this.y;
        return FastMath.sqrt(dx * dx + dy * dy);
    }
    
    public double distanceInf(final Vector<Euclidean2D> p) {
        final Vector2D p2 = (Vector2D)p;
        final double dx = FastMath.abs(p2.x - this.x);
        final double dy = FastMath.abs(p2.y - this.y);
        return FastMath.max(dx, dy);
    }
    
    public double distanceSq(final Vector<Euclidean2D> p) {
        final Vector2D p2 = (Vector2D)p;
        final double dx = p2.x - this.x;
        final double dy = p2.y - this.y;
        return dx * dx + dy * dy;
    }
    
    public double dotProduct(final Vector<Euclidean2D> v) {
        final Vector2D v2 = (Vector2D)v;
        return MathArrays.linearCombination(this.x, v2.x, this.y, v2.y);
    }
    
    public double crossProduct(final Vector2D p1, final Vector2D p2) {
        final double x1 = p2.getX() - p1.getX();
        final double y1 = this.getY() - p1.getY();
        final double x2 = this.getX() - p1.getX();
        final double y2 = p2.getY() - p1.getY();
        return MathArrays.linearCombination(x1, y1, -x2, y2);
    }
    
    public static double distance(final Vector2D p1, final Vector2D p2) {
        return p1.distance(p2);
    }
    
    public static double distanceInf(final Vector2D p1, final Vector2D p2) {
        return p1.distanceInf(p2);
    }
    
    public static double distanceSq(final Vector2D p1, final Vector2D p2) {
        return p1.distanceSq(p2);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vector2D)) {
            return false;
        }
        final Vector2D rhs = (Vector2D)other;
        if (rhs.isNaN()) {
            return this.isNaN();
        }
        return this.x == rhs.x && this.y == rhs.y;
    }
    
    @Override
    public int hashCode() {
        if (this.isNaN()) {
            return 542;
        }
        return 122 * (76 * MathUtils.hash(this.x) + MathUtils.hash(this.y));
    }
    
    @Override
    public String toString() {
        return Vector2DFormat.getInstance().format(this);
    }
    
    public String toString(final NumberFormat format) {
        return new Vector2DFormat(format).format(this);
    }
    
    static {
        ZERO = new Vector2D(0.0, 0.0);
        NaN = new Vector2D(Double.NaN, Double.NaN);
        POSITIVE_INFINITY = new Vector2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        NEGATIVE_INFINITY = new Vector2D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }
}
