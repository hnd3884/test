package org.apache.commons.math3.geometry.euclidean.threed;

import java.text.NumberFormat;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.io.Serializable;
import org.apache.commons.math3.RealFieldElement;

public class FieldVector3D<T extends RealFieldElement<T>> implements Serializable
{
    private static final long serialVersionUID = 20130224L;
    private final T x;
    private final T y;
    private final T z;
    
    public FieldVector3D(final T x, final T y, final T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public FieldVector3D(final T[] v) throws DimensionMismatchException {
        if (v.length != 3) {
            throw new DimensionMismatchException(v.length, 3);
        }
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
    }
    
    public FieldVector3D(final T alpha, final T delta) {
        final T cosDelta = delta.cos();
        this.x = ((RealFieldElement<RealFieldElement<T>>)alpha).cos().multiply(cosDelta);
        this.y = ((RealFieldElement<RealFieldElement<T>>)alpha).sin().multiply(cosDelta);
        this.z = delta.sin();
    }
    
    public FieldVector3D(final T a, final FieldVector3D<T> u) {
        this.x = a.multiply(u.x);
        this.y = a.multiply(u.y);
        this.z = a.multiply(u.z);
    }
    
    public FieldVector3D(final T a, final Vector3D u) {
        this.x = a.multiply(u.getX());
        this.y = a.multiply(u.getY());
        this.z = a.multiply(u.getZ());
    }
    
    public FieldVector3D(final double a, final FieldVector3D<T> u) {
        this.x = u.x.multiply(a);
        this.y = u.y.multiply(a);
        this.z = u.z.multiply(a);
    }
    
    public FieldVector3D(final T a1, final FieldVector3D<T> u1, final T a2, final FieldVector3D<T> u2) {
        final T prototype = a1;
        this.x = prototype.linearCombination(a1, u1.getX(), a2, u2.getX());
        this.y = prototype.linearCombination(a1, u1.getY(), a2, u2.getY());
        this.z = prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ());
    }
    
    public FieldVector3D(final T a1, final Vector3D u1, final T a2, final Vector3D u2) {
        final T prototype = a1;
        this.x = prototype.linearCombination(u1.getX(), a1, u2.getX(), a2);
        this.y = prototype.linearCombination(u1.getY(), a1, u2.getY(), a2);
        this.z = prototype.linearCombination(u1.getZ(), a1, u2.getZ(), a2);
    }
    
    public FieldVector3D(final double a1, final FieldVector3D<T> u1, final double a2, final FieldVector3D<T> u2) {
        final T prototype = u1.getX();
        this.x = prototype.linearCombination(a1, u1.getX(), a2, u2.getX());
        this.y = prototype.linearCombination(a1, u1.getY(), a2, u2.getY());
        this.z = prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ());
    }
    
    public FieldVector3D(final T a1, final FieldVector3D<T> u1, final T a2, final FieldVector3D<T> u2, final T a3, final FieldVector3D<T> u3) {
        final T prototype = a1;
        this.x = prototype.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX());
        this.y = prototype.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY());
        this.z = prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ());
    }
    
    public FieldVector3D(final T a1, final Vector3D u1, final T a2, final Vector3D u2, final T a3, final Vector3D u3) {
        final T prototype = a1;
        this.x = prototype.linearCombination(u1.getX(), a1, u2.getX(), a2, u3.getX(), a3);
        this.y = prototype.linearCombination(u1.getY(), a1, u2.getY(), a2, u3.getY(), a3);
        this.z = prototype.linearCombination(u1.getZ(), a1, u2.getZ(), a2, u3.getZ(), a3);
    }
    
    public FieldVector3D(final double a1, final FieldVector3D<T> u1, final double a2, final FieldVector3D<T> u2, final double a3, final FieldVector3D<T> u3) {
        final T prototype = u1.getX();
        this.x = prototype.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX());
        this.y = prototype.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY());
        this.z = prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ());
    }
    
    public FieldVector3D(final T a1, final FieldVector3D<T> u1, final T a2, final FieldVector3D<T> u2, final T a3, final FieldVector3D<T> u3, final T a4, final FieldVector3D<T> u4) {
        final T prototype = a1;
        this.x = prototype.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX(), a4, u4.getX());
        this.y = prototype.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY(), a4, u4.getY());
        this.z = prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ(), a4, u4.getZ());
    }
    
    public FieldVector3D(final T a1, final Vector3D u1, final T a2, final Vector3D u2, final T a3, final Vector3D u3, final T a4, final Vector3D u4) {
        final T prototype = a1;
        this.x = prototype.linearCombination(u1.getX(), a1, u2.getX(), a2, u3.getX(), a3, u4.getX(), a4);
        this.y = prototype.linearCombination(u1.getY(), a1, u2.getY(), a2, u3.getY(), a3, u4.getY(), a4);
        this.z = prototype.linearCombination(u1.getZ(), a1, u2.getZ(), a2, u3.getZ(), a3, u4.getZ(), a4);
    }
    
    public FieldVector3D(final double a1, final FieldVector3D<T> u1, final double a2, final FieldVector3D<T> u2, final double a3, final FieldVector3D<T> u3, final double a4, final FieldVector3D<T> u4) {
        final T prototype = u1.getX();
        this.x = prototype.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX(), a4, u4.getX());
        this.y = prototype.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY(), a4, u4.getY());
        this.z = prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ(), a4, u4.getZ());
    }
    
    public T getX() {
        return this.x;
    }
    
    public T getY() {
        return this.y;
    }
    
    public T getZ() {
        return this.z;
    }
    
    public T[] toArray() {
        final T[] array = MathArrays.buildArray(this.x.getField(), 3);
        array[0] = this.x;
        array[1] = this.y;
        array[2] = this.z;
        return array;
    }
    
    public Vector3D toVector3D() {
        return new Vector3D(this.x.getReal(), this.y.getReal(), this.z.getReal());
    }
    
    public T getNorm1() {
        return (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.x).abs().add(((RealFieldElement<RealFieldElement>)this.y).abs()).add(((RealFieldElement<Object>)this.z).abs());
    }
    
    public T getNorm() {
        return this.x.multiply(this.x).add(this.y.multiply(this.y)).add(this.z.multiply(this.z)).sqrt();
    }
    
    public T getNormSq() {
        return this.x.multiply(this.x).add(this.y.multiply(this.y)).add(this.z.multiply(this.z));
    }
    
    public T getNormInf() {
        final T xAbs = this.x.abs();
        final T yAbs = this.y.abs();
        final T zAbs = this.z.abs();
        if (xAbs.getReal() <= yAbs.getReal()) {
            if (yAbs.getReal() <= zAbs.getReal()) {
                return zAbs;
            }
            return yAbs;
        }
        else {
            if (xAbs.getReal() <= zAbs.getReal()) {
                return zAbs;
            }
            return xAbs;
        }
    }
    
    public T getAlpha() {
        return this.y.atan2(this.x);
    }
    
    public T getDelta() {
        return this.z.divide(this.getNorm()).asin();
    }
    
    public FieldVector3D<T> add(final FieldVector3D<T> v) {
        return new FieldVector3D<T>(this.x.add(v.x), this.y.add(v.y), this.z.add(v.z));
    }
    
    public FieldVector3D<T> add(final Vector3D v) {
        return new FieldVector3D<T>(this.x.add(v.getX()), this.y.add(v.getY()), this.z.add(v.getZ()));
    }
    
    public FieldVector3D<T> add(final T factor, final FieldVector3D<T> v) {
        return new FieldVector3D<T>(this.x.getField().getOne(), this, factor, v);
    }
    
    public FieldVector3D<T> add(final T factor, final Vector3D v) {
        return new FieldVector3D<T>(this.x.add(factor.multiply(v.getX())), this.y.add(factor.multiply(v.getY())), this.z.add(factor.multiply(v.getZ())));
    }
    
    public FieldVector3D<T> add(final double factor, final FieldVector3D<T> v) {
        return new FieldVector3D<T>(1.0, this, factor, v);
    }
    
    public FieldVector3D<T> add(final double factor, final Vector3D v) {
        return new FieldVector3D<T>(this.x.add(factor * v.getX()), this.y.add(factor * v.getY()), this.z.add(factor * v.getZ()));
    }
    
    public FieldVector3D<T> subtract(final FieldVector3D<T> v) {
        return new FieldVector3D<T>(this.x.subtract(v.x), this.y.subtract(v.y), this.z.subtract(v.z));
    }
    
    public FieldVector3D<T> subtract(final Vector3D v) {
        return new FieldVector3D<T>(this.x.subtract(v.getX()), this.y.subtract(v.getY()), this.z.subtract(v.getZ()));
    }
    
    public FieldVector3D<T> subtract(final T factor, final FieldVector3D<T> v) {
        return new FieldVector3D<T>(this.x.getField().getOne(), this, factor.negate(), v);
    }
    
    public FieldVector3D<T> subtract(final T factor, final Vector3D v) {
        return new FieldVector3D<T>(this.x.subtract(factor.multiply(v.getX())), this.y.subtract(factor.multiply(v.getY())), this.z.subtract(factor.multiply(v.getZ())));
    }
    
    public FieldVector3D<T> subtract(final double factor, final FieldVector3D<T> v) {
        return new FieldVector3D<T>(1.0, this, -factor, v);
    }
    
    public FieldVector3D<T> subtract(final double factor, final Vector3D v) {
        return new FieldVector3D<T>(this.x.subtract(factor * v.getX()), this.y.subtract(factor * v.getY()), this.z.subtract(factor * v.getZ()));
    }
    
    public FieldVector3D<T> normalize() throws MathArithmeticException {
        final T s = this.getNorm();
        if (s.getReal() == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR, new Object[0]);
        }
        return this.scalarMultiply(s.reciprocal());
    }
    
    public FieldVector3D<T> orthogonal() throws MathArithmeticException {
        final double threshold = 0.6 * this.getNorm().getReal();
        if (threshold == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        if (FastMath.abs(this.x.getReal()) <= threshold) {
            final T inverse = ((RealFieldElement<RealFieldElement<T>>)this.y.multiply(this.y).add(this.z.multiply(this.z))).sqrt().reciprocal();
            return new FieldVector3D<T>(inverse.getField().getZero(), inverse.multiply(this.z), inverse.multiply(this.y).negate());
        }
        if (FastMath.abs(this.y.getReal()) <= threshold) {
            final T inverse = ((RealFieldElement<RealFieldElement<T>>)this.x.multiply(this.x).add(this.z.multiply(this.z))).sqrt().reciprocal();
            return new FieldVector3D<T>(inverse.multiply(this.z).negate(), inverse.getField().getZero(), inverse.multiply(this.x));
        }
        final T inverse = ((RealFieldElement<RealFieldElement<T>>)this.x.multiply(this.x).add(this.y.multiply(this.y))).sqrt().reciprocal();
        return new FieldVector3D<T>(inverse.multiply(this.y), inverse.multiply(this.x).negate(), inverse.getField().getZero());
    }
    
    public static <T extends RealFieldElement<T>> T angle(final FieldVector3D<T> v1, final FieldVector3D<T> v2) throws MathArithmeticException {
        final T normProduct = v1.getNorm().multiply(v2.getNorm());
        if (normProduct.getReal() == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        final T dot = (T)dotProduct((FieldVector3D<RealFieldElement>)v1, (FieldVector3D<RealFieldElement>)v2);
        final double threshold = normProduct.getReal() * 0.9999;
        if (dot.getReal() >= -threshold && dot.getReal() <= threshold) {
            return dot.divide(normProduct).acos();
        }
        final FieldVector3D<T> v3 = crossProduct(v1, v2);
        if (dot.getReal() >= 0.0) {
            return v3.getNorm().divide(normProduct).asin();
        }
        return ((RealFieldElement<RealFieldElement<RealFieldElement>>)v3.getNorm().divide(normProduct)).asin().subtract(3.141592653589793).negate();
    }
    
    public static <T extends RealFieldElement<T>> T angle(final FieldVector3D<T> v1, final Vector3D v2) throws MathArithmeticException {
        final T normProduct = v1.getNorm().multiply(v2.getNorm());
        if (normProduct.getReal() == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        final T dot = (T)dotProduct((FieldVector3D<RealFieldElement>)v1, v2);
        final double threshold = normProduct.getReal() * 0.9999;
        if (dot.getReal() >= -threshold && dot.getReal() <= threshold) {
            return dot.divide(normProduct).acos();
        }
        final FieldVector3D<T> v3 = crossProduct(v1, v2);
        if (dot.getReal() >= 0.0) {
            return v3.getNorm().divide(normProduct).asin();
        }
        return ((RealFieldElement<RealFieldElement<RealFieldElement>>)v3.getNorm().divide(normProduct)).asin().subtract(3.141592653589793).negate();
    }
    
    public static <T extends RealFieldElement<T>> T angle(final Vector3D v1, final FieldVector3D<T> v2) throws MathArithmeticException {
        return angle(v2, v1);
    }
    
    public FieldVector3D<T> negate() {
        return new FieldVector3D<T>(this.x.negate(), this.y.negate(), this.z.negate());
    }
    
    public FieldVector3D<T> scalarMultiply(final T a) {
        return new FieldVector3D<T>(this.x.multiply(a), this.y.multiply(a), this.z.multiply(a));
    }
    
    public FieldVector3D<T> scalarMultiply(final double a) {
        return new FieldVector3D<T>(this.x.multiply(a), this.y.multiply(a), this.z.multiply(a));
    }
    
    public boolean isNaN() {
        return Double.isNaN(this.x.getReal()) || Double.isNaN(this.y.getReal()) || Double.isNaN(this.z.getReal());
    }
    
    public boolean isInfinite() {
        return !this.isNaN() && (Double.isInfinite(this.x.getReal()) || Double.isInfinite(this.y.getReal()) || Double.isInfinite(this.z.getReal()));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FieldVector3D)) {
            return false;
        }
        final FieldVector3D<T> rhs = (FieldVector3D<T>)other;
        if (rhs.isNaN()) {
            return this.isNaN();
        }
        return this.x.equals(rhs.x) && this.y.equals(rhs.y) && this.z.equals(rhs.z);
    }
    
    @Override
    public int hashCode() {
        if (this.isNaN()) {
            return 409;
        }
        return 311 * (107 * this.x.hashCode() + 83 * this.y.hashCode() + this.z.hashCode());
    }
    
    public T dotProduct(final FieldVector3D<T> v) {
        return this.x.linearCombination(this.x, v.x, this.y, v.y, this.z, v.z);
    }
    
    public T dotProduct(final Vector3D v) {
        return this.x.linearCombination(v.getX(), this.x, v.getY(), this.y, v.getZ(), this.z);
    }
    
    public FieldVector3D<T> crossProduct(final FieldVector3D<T> v) {
        return new FieldVector3D<T>(this.x.linearCombination(this.y, v.z, this.z.negate(), v.y), this.y.linearCombination(this.z, v.x, this.x.negate(), v.z), this.z.linearCombination(this.x, v.y, this.y.negate(), v.x));
    }
    
    public FieldVector3D<T> crossProduct(final Vector3D v) {
        return new FieldVector3D<T>(this.x.linearCombination(v.getZ(), this.y, -v.getY(), this.z), this.y.linearCombination(v.getX(), this.z, -v.getZ(), this.x), this.z.linearCombination(v.getY(), this.x, -v.getX(), this.y));
    }
    
    public T distance1(final FieldVector3D<T> v) {
        final T dx = v.x.subtract(this.x).abs();
        final T dy = v.y.subtract(this.y).abs();
        final T dz = v.z.subtract(this.z).abs();
        return dx.add(dy).add(dz);
    }
    
    public T distance1(final Vector3D v) {
        final T dx = ((RealFieldElement<RealFieldElement<T>>)this.x).subtract(v.getX()).abs();
        final T dy = ((RealFieldElement<RealFieldElement<T>>)this.y).subtract(v.getY()).abs();
        final T dz = ((RealFieldElement<RealFieldElement<T>>)this.z).subtract(v.getZ()).abs();
        return dx.add(dy).add(dz);
    }
    
    public T distance(final FieldVector3D<T> v) {
        final T dx = v.x.subtract(this.x);
        final T dy = v.y.subtract(this.y);
        final T dz = v.z.subtract(this.z);
        return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz)).sqrt();
    }
    
    public T distance(final Vector3D v) {
        final T dx = this.x.subtract(v.getX());
        final T dy = this.y.subtract(v.getY());
        final T dz = this.z.subtract(v.getZ());
        return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz)).sqrt();
    }
    
    public T distanceInf(final FieldVector3D<T> v) {
        final T dx = v.x.subtract(this.x).abs();
        final T dy = v.y.subtract(this.y).abs();
        final T dz = v.z.subtract(this.z).abs();
        if (dx.getReal() <= dy.getReal()) {
            if (dy.getReal() <= dz.getReal()) {
                return dz;
            }
            return dy;
        }
        else {
            if (dx.getReal() <= dz.getReal()) {
                return dz;
            }
            return dx;
        }
    }
    
    public T distanceInf(final Vector3D v) {
        final T dx = ((RealFieldElement<RealFieldElement<T>>)this.x).subtract(v.getX()).abs();
        final T dy = ((RealFieldElement<RealFieldElement<T>>)this.y).subtract(v.getY()).abs();
        final T dz = ((RealFieldElement<RealFieldElement<T>>)this.z).subtract(v.getZ()).abs();
        if (dx.getReal() <= dy.getReal()) {
            if (dy.getReal() <= dz.getReal()) {
                return dz;
            }
            return dy;
        }
        else {
            if (dx.getReal() <= dz.getReal()) {
                return dz;
            }
            return dx;
        }
    }
    
    public T distanceSq(final FieldVector3D<T> v) {
        final T dx = v.x.subtract(this.x);
        final T dy = v.y.subtract(this.y);
        final T dz = v.z.subtract(this.z);
        return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz));
    }
    
    public T distanceSq(final Vector3D v) {
        final T dx = this.x.subtract(v.getX());
        final T dy = this.y.subtract(v.getY());
        final T dz = this.z.subtract(v.getZ());
        return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz));
    }
    
    public static <T extends RealFieldElement<T>> T dotProduct(final FieldVector3D<T> v1, final FieldVector3D<T> v2) {
        return v1.dotProduct(v2);
    }
    
    public static <T extends RealFieldElement<T>> T dotProduct(final FieldVector3D<T> v1, final Vector3D v2) {
        return v1.dotProduct(v2);
    }
    
    public static <T extends RealFieldElement<T>> T dotProduct(final Vector3D v1, final FieldVector3D<T> v2) {
        return v2.dotProduct(v1);
    }
    
    public static <T extends RealFieldElement<T>> FieldVector3D<T> crossProduct(final FieldVector3D<T> v1, final FieldVector3D<T> v2) {
        return v1.crossProduct(v2);
    }
    
    public static <T extends RealFieldElement<T>> FieldVector3D<T> crossProduct(final FieldVector3D<T> v1, final Vector3D v2) {
        return v1.crossProduct(v2);
    }
    
    public static <T extends RealFieldElement<T>> FieldVector3D<T> crossProduct(final Vector3D v1, final FieldVector3D<T> v2) {
        return new FieldVector3D<T>(v2.x.linearCombination(v1.getY(), v2.z, -v1.getZ(), v2.y), v2.y.linearCombination(v1.getZ(), v2.x, -v1.getX(), v2.z), v2.z.linearCombination(v1.getX(), v2.y, -v1.getY(), v2.x));
    }
    
    public static <T extends RealFieldElement<T>> T distance1(final FieldVector3D<T> v1, final FieldVector3D<T> v2) {
        return v1.distance1(v2);
    }
    
    public static <T extends RealFieldElement<T>> T distance1(final FieldVector3D<T> v1, final Vector3D v2) {
        return v1.distance1(v2);
    }
    
    public static <T extends RealFieldElement<T>> T distance1(final Vector3D v1, final FieldVector3D<T> v2) {
        return v2.distance1(v1);
    }
    
    public static <T extends RealFieldElement<T>> T distance(final FieldVector3D<T> v1, final FieldVector3D<T> v2) {
        return v1.distance(v2);
    }
    
    public static <T extends RealFieldElement<T>> T distance(final FieldVector3D<T> v1, final Vector3D v2) {
        return v1.distance(v2);
    }
    
    public static <T extends RealFieldElement<T>> T distance(final Vector3D v1, final FieldVector3D<T> v2) {
        return v2.distance(v1);
    }
    
    public static <T extends RealFieldElement<T>> T distanceInf(final FieldVector3D<T> v1, final FieldVector3D<T> v2) {
        return v1.distanceInf(v2);
    }
    
    public static <T extends RealFieldElement<T>> T distanceInf(final FieldVector3D<T> v1, final Vector3D v2) {
        return v1.distanceInf(v2);
    }
    
    public static <T extends RealFieldElement<T>> T distanceInf(final Vector3D v1, final FieldVector3D<T> v2) {
        return v2.distanceInf(v1);
    }
    
    public static <T extends RealFieldElement<T>> T distanceSq(final FieldVector3D<T> v1, final FieldVector3D<T> v2) {
        return v1.distanceSq(v2);
    }
    
    public static <T extends RealFieldElement<T>> T distanceSq(final FieldVector3D<T> v1, final Vector3D v2) {
        return v1.distanceSq(v2);
    }
    
    public static <T extends RealFieldElement<T>> T distanceSq(final Vector3D v1, final FieldVector3D<T> v2) {
        return v2.distanceSq(v1);
    }
    
    @Override
    public String toString() {
        return Vector3DFormat.getInstance().format(this.toVector3D());
    }
    
    public String toString(final NumberFormat format) {
        return new Vector3DFormat(format).format(this.toVector3D());
    }
}
