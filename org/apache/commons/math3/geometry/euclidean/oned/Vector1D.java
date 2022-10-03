package org.apache.commons.math3.geometry.euclidean.oned;

import java.text.NumberFormat;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.Vector;

public class Vector1D implements Vector<Euclidean1D>
{
    public static final Vector1D ZERO;
    public static final Vector1D ONE;
    public static final Vector1D NaN;
    public static final Vector1D POSITIVE_INFINITY;
    public static final Vector1D NEGATIVE_INFINITY;
    private static final long serialVersionUID = 7556674948671647925L;
    private final double x;
    
    public Vector1D(final double x) {
        this.x = x;
    }
    
    public Vector1D(final double a, final Vector1D u) {
        this.x = a * u.x;
    }
    
    public Vector1D(final double a1, final Vector1D u1, final double a2, final Vector1D u2) {
        this.x = a1 * u1.x + a2 * u2.x;
    }
    
    public Vector1D(final double a1, final Vector1D u1, final double a2, final Vector1D u2, final double a3, final Vector1D u3) {
        this.x = a1 * u1.x + a2 * u2.x + a3 * u3.x;
    }
    
    public Vector1D(final double a1, final Vector1D u1, final double a2, final Vector1D u2, final double a3, final Vector1D u3, final double a4, final Vector1D u4) {
        this.x = a1 * u1.x + a2 * u2.x + a3 * u3.x + a4 * u4.x;
    }
    
    public double getX() {
        return this.x;
    }
    
    public Space getSpace() {
        return Euclidean1D.getInstance();
    }
    
    public Vector1D getZero() {
        return Vector1D.ZERO;
    }
    
    public double getNorm1() {
        return FastMath.abs(this.x);
    }
    
    public double getNorm() {
        return FastMath.abs(this.x);
    }
    
    public double getNormSq() {
        return this.x * this.x;
    }
    
    public double getNormInf() {
        return FastMath.abs(this.x);
    }
    
    public Vector1D add(final Vector<Euclidean1D> v) {
        final Vector1D v2 = (Vector1D)v;
        return new Vector1D(this.x + v2.getX());
    }
    
    public Vector1D add(final double factor, final Vector<Euclidean1D> v) {
        final Vector1D v2 = (Vector1D)v;
        return new Vector1D(this.x + factor * v2.getX());
    }
    
    public Vector1D subtract(final Vector<Euclidean1D> p) {
        final Vector1D p2 = (Vector1D)p;
        return new Vector1D(this.x - p2.x);
    }
    
    public Vector1D subtract(final double factor, final Vector<Euclidean1D> v) {
        final Vector1D v2 = (Vector1D)v;
        return new Vector1D(this.x - factor * v2.getX());
    }
    
    public Vector1D normalize() throws MathArithmeticException {
        final double s = this.getNorm();
        if (s == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR, new Object[0]);
        }
        return this.scalarMultiply(1.0 / s);
    }
    
    public Vector1D negate() {
        return new Vector1D(-this.x);
    }
    
    public Vector1D scalarMultiply(final double a) {
        return new Vector1D(a * this.x);
    }
    
    public boolean isNaN() {
        return Double.isNaN(this.x);
    }
    
    public boolean isInfinite() {
        return !this.isNaN() && Double.isInfinite(this.x);
    }
    
    public double distance1(final Vector<Euclidean1D> p) {
        final Vector1D p2 = (Vector1D)p;
        final double dx = FastMath.abs(p2.x - this.x);
        return dx;
    }
    
    @Deprecated
    public double distance(final Vector<Euclidean1D> p) {
        return this.distance((Point<Euclidean1D>)p);
    }
    
    public double distance(final Point<Euclidean1D> p) {
        final Vector1D p2 = (Vector1D)p;
        final double dx = p2.x - this.x;
        return FastMath.abs(dx);
    }
    
    public double distanceInf(final Vector<Euclidean1D> p) {
        final Vector1D p2 = (Vector1D)p;
        final double dx = FastMath.abs(p2.x - this.x);
        return dx;
    }
    
    public double distanceSq(final Vector<Euclidean1D> p) {
        final Vector1D p2 = (Vector1D)p;
        final double dx = p2.x - this.x;
        return dx * dx;
    }
    
    public double dotProduct(final Vector<Euclidean1D> v) {
        final Vector1D v2 = (Vector1D)v;
        return this.x * v2.x;
    }
    
    public static double distance(final Vector1D p1, final Vector1D p2) {
        return p1.distance(p2);
    }
    
    public static double distanceInf(final Vector1D p1, final Vector1D p2) {
        return p1.distanceInf(p2);
    }
    
    public static double distanceSq(final Vector1D p1, final Vector1D p2) {
        return p1.distanceSq(p2);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vector1D)) {
            return false;
        }
        final Vector1D rhs = (Vector1D)other;
        if (rhs.isNaN()) {
            return this.isNaN();
        }
        return this.x == rhs.x;
    }
    
    @Override
    public int hashCode() {
        if (this.isNaN()) {
            return 7785;
        }
        return 997 * MathUtils.hash(this.x);
    }
    
    @Override
    public String toString() {
        return Vector1DFormat.getInstance().format(this);
    }
    
    public String toString(final NumberFormat format) {
        return new Vector1DFormat(format).format(this);
    }
    
    static {
        ZERO = new Vector1D(0.0);
        ONE = new Vector1D(1.0);
        NaN = new Vector1D(Double.NaN);
        POSITIVE_INFINITY = new Vector1D(Double.POSITIVE_INFINITY);
        NEGATIVE_INFINITY = new Vector1D(Double.NEGATIVE_INFINITY);
    }
}
