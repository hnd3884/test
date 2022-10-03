package org.apache.commons.math3.geometry.euclidean.threed;

import java.text.NumberFormat;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.geometry.Vector;
import java.io.Serializable;

public class Vector3D implements Serializable, Vector<Euclidean3D>
{
    public static final Vector3D ZERO;
    public static final Vector3D PLUS_I;
    public static final Vector3D MINUS_I;
    public static final Vector3D PLUS_J;
    public static final Vector3D MINUS_J;
    public static final Vector3D PLUS_K;
    public static final Vector3D MINUS_K;
    public static final Vector3D NaN;
    public static final Vector3D POSITIVE_INFINITY;
    public static final Vector3D NEGATIVE_INFINITY;
    private static final long serialVersionUID = 1313493323784566947L;
    private final double x;
    private final double y;
    private final double z;
    
    public Vector3D(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3D(final double[] v) throws DimensionMismatchException {
        if (v.length != 3) {
            throw new DimensionMismatchException(v.length, 3);
        }
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
    }
    
    public Vector3D(final double alpha, final double delta) {
        final double cosDelta = FastMath.cos(delta);
        this.x = FastMath.cos(alpha) * cosDelta;
        this.y = FastMath.sin(alpha) * cosDelta;
        this.z = FastMath.sin(delta);
    }
    
    public Vector3D(final double a, final Vector3D u) {
        this.x = a * u.x;
        this.y = a * u.y;
        this.z = a * u.z;
    }
    
    public Vector3D(final double a1, final Vector3D u1, final double a2, final Vector3D u2) {
        this.x = MathArrays.linearCombination(a1, u1.x, a2, u2.x);
        this.y = MathArrays.linearCombination(a1, u1.y, a2, u2.y);
        this.z = MathArrays.linearCombination(a1, u1.z, a2, u2.z);
    }
    
    public Vector3D(final double a1, final Vector3D u1, final double a2, final Vector3D u2, final double a3, final Vector3D u3) {
        this.x = MathArrays.linearCombination(a1, u1.x, a2, u2.x, a3, u3.x);
        this.y = MathArrays.linearCombination(a1, u1.y, a2, u2.y, a3, u3.y);
        this.z = MathArrays.linearCombination(a1, u1.z, a2, u2.z, a3, u3.z);
    }
    
    public Vector3D(final double a1, final Vector3D u1, final double a2, final Vector3D u2, final double a3, final Vector3D u3, final double a4, final Vector3D u4) {
        this.x = MathArrays.linearCombination(a1, u1.x, a2, u2.x, a3, u3.x, a4, u4.x);
        this.y = MathArrays.linearCombination(a1, u1.y, a2, u2.y, a3, u3.y, a4, u4.y);
        this.z = MathArrays.linearCombination(a1, u1.z, a2, u2.z, a3, u3.z, a4, u4.z);
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public double[] toArray() {
        return new double[] { this.x, this.y, this.z };
    }
    
    public Space getSpace() {
        return Euclidean3D.getInstance();
    }
    
    public Vector3D getZero() {
        return Vector3D.ZERO;
    }
    
    public double getNorm1() {
        return FastMath.abs(this.x) + FastMath.abs(this.y) + FastMath.abs(this.z);
    }
    
    public double getNorm() {
        return FastMath.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public double getNormSq() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public double getNormInf() {
        return FastMath.max(FastMath.max(FastMath.abs(this.x), FastMath.abs(this.y)), FastMath.abs(this.z));
    }
    
    public double getAlpha() {
        return FastMath.atan2(this.y, this.x);
    }
    
    public double getDelta() {
        return FastMath.asin(this.z / this.getNorm());
    }
    
    public Vector3D add(final Vector<Euclidean3D> v) {
        final Vector3D v2 = (Vector3D)v;
        return new Vector3D(this.x + v2.x, this.y + v2.y, this.z + v2.z);
    }
    
    public Vector3D add(final double factor, final Vector<Euclidean3D> v) {
        return new Vector3D(1.0, this, factor, (Vector3D)v);
    }
    
    public Vector3D subtract(final Vector<Euclidean3D> v) {
        final Vector3D v2 = (Vector3D)v;
        return new Vector3D(this.x - v2.x, this.y - v2.y, this.z - v2.z);
    }
    
    public Vector3D subtract(final double factor, final Vector<Euclidean3D> v) {
        return new Vector3D(1.0, this, -factor, (Vector3D)v);
    }
    
    public Vector3D normalize() throws MathArithmeticException {
        final double s = this.getNorm();
        if (s == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR, new Object[0]);
        }
        return this.scalarMultiply(1.0 / s);
    }
    
    public Vector3D orthogonal() throws MathArithmeticException {
        final double threshold = 0.6 * this.getNorm();
        if (threshold == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        if (FastMath.abs(this.x) <= threshold) {
            final double inverse = 1.0 / FastMath.sqrt(this.y * this.y + this.z * this.z);
            return new Vector3D(0.0, inverse * this.z, -inverse * this.y);
        }
        if (FastMath.abs(this.y) <= threshold) {
            final double inverse = 1.0 / FastMath.sqrt(this.x * this.x + this.z * this.z);
            return new Vector3D(-inverse * this.z, 0.0, inverse * this.x);
        }
        final double inverse = 1.0 / FastMath.sqrt(this.x * this.x + this.y * this.y);
        return new Vector3D(inverse * this.y, -inverse * this.x, 0.0);
    }
    
    public static double angle(final Vector3D v1, final Vector3D v2) throws MathArithmeticException {
        final double normProduct = v1.getNorm() * v2.getNorm();
        if (normProduct == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        final double dot = v1.dotProduct(v2);
        final double threshold = normProduct * 0.9999;
        if (dot >= -threshold && dot <= threshold) {
            return FastMath.acos(dot / normProduct);
        }
        final Vector3D v3 = crossProduct(v1, v2);
        if (dot >= 0.0) {
            return FastMath.asin(v3.getNorm() / normProduct);
        }
        return 3.141592653589793 - FastMath.asin(v3.getNorm() / normProduct);
    }
    
    public Vector3D negate() {
        return new Vector3D(-this.x, -this.y, -this.z);
    }
    
    public Vector3D scalarMultiply(final double a) {
        return new Vector3D(a * this.x, a * this.y, a * this.z);
    }
    
    public boolean isNaN() {
        return Double.isNaN(this.x) || Double.isNaN(this.y) || Double.isNaN(this.z);
    }
    
    public boolean isInfinite() {
        return !this.isNaN() && (Double.isInfinite(this.x) || Double.isInfinite(this.y) || Double.isInfinite(this.z));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vector3D)) {
            return false;
        }
        final Vector3D rhs = (Vector3D)other;
        if (rhs.isNaN()) {
            return this.isNaN();
        }
        return this.x == rhs.x && this.y == rhs.y && this.z == rhs.z;
    }
    
    @Override
    public int hashCode() {
        if (this.isNaN()) {
            return 642;
        }
        return 643 * (164 * MathUtils.hash(this.x) + 3 * MathUtils.hash(this.y) + MathUtils.hash(this.z));
    }
    
    public double dotProduct(final Vector<Euclidean3D> v) {
        final Vector3D v2 = (Vector3D)v;
        return MathArrays.linearCombination(this.x, v2.x, this.y, v2.y, this.z, v2.z);
    }
    
    public Vector3D crossProduct(final Vector<Euclidean3D> v) {
        final Vector3D v2 = (Vector3D)v;
        return new Vector3D(MathArrays.linearCombination(this.y, v2.z, -this.z, v2.y), MathArrays.linearCombination(this.z, v2.x, -this.x, v2.z), MathArrays.linearCombination(this.x, v2.y, -this.y, v2.x));
    }
    
    public double distance1(final Vector<Euclidean3D> v) {
        final Vector3D v2 = (Vector3D)v;
        final double dx = FastMath.abs(v2.x - this.x);
        final double dy = FastMath.abs(v2.y - this.y);
        final double dz = FastMath.abs(v2.z - this.z);
        return dx + dy + dz;
    }
    
    public double distance(final Vector<Euclidean3D> v) {
        return this.distance((Point<Euclidean3D>)v);
    }
    
    public double distance(final Point<Euclidean3D> v) {
        final Vector3D v2 = (Vector3D)v;
        final double dx = v2.x - this.x;
        final double dy = v2.y - this.y;
        final double dz = v2.z - this.z;
        return FastMath.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public double distanceInf(final Vector<Euclidean3D> v) {
        final Vector3D v2 = (Vector3D)v;
        final double dx = FastMath.abs(v2.x - this.x);
        final double dy = FastMath.abs(v2.y - this.y);
        final double dz = FastMath.abs(v2.z - this.z);
        return FastMath.max(FastMath.max(dx, dy), dz);
    }
    
    public double distanceSq(final Vector<Euclidean3D> v) {
        final Vector3D v2 = (Vector3D)v;
        final double dx = v2.x - this.x;
        final double dy = v2.y - this.y;
        final double dz = v2.z - this.z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    public static double dotProduct(final Vector3D v1, final Vector3D v2) {
        return v1.dotProduct(v2);
    }
    
    public static Vector3D crossProduct(final Vector3D v1, final Vector3D v2) {
        return v1.crossProduct(v2);
    }
    
    public static double distance1(final Vector3D v1, final Vector3D v2) {
        return v1.distance1(v2);
    }
    
    public static double distance(final Vector3D v1, final Vector3D v2) {
        return v1.distance(v2);
    }
    
    public static double distanceInf(final Vector3D v1, final Vector3D v2) {
        return v1.distanceInf(v2);
    }
    
    public static double distanceSq(final Vector3D v1, final Vector3D v2) {
        return v1.distanceSq(v2);
    }
    
    @Override
    public String toString() {
        return Vector3DFormat.getInstance().format(this);
    }
    
    public String toString(final NumberFormat format) {
        return new Vector3DFormat(format).format(this);
    }
    
    static {
        ZERO = new Vector3D(0.0, 0.0, 0.0);
        PLUS_I = new Vector3D(1.0, 0.0, 0.0);
        MINUS_I = new Vector3D(-1.0, 0.0, 0.0);
        PLUS_J = new Vector3D(0.0, 1.0, 0.0);
        MINUS_J = new Vector3D(0.0, -1.0, 0.0);
        PLUS_K = new Vector3D(0.0, 0.0, 1.0);
        MINUS_K = new Vector3D(0.0, 0.0, -1.0);
        NaN = new Vector3D(Double.NaN, Double.NaN, Double.NaN);
        POSITIVE_INFINITY = new Vector3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        NEGATIVE_INFINITY = new Vector3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }
}
