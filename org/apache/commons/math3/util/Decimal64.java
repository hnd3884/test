package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

public class Decimal64 extends Number implements RealFieldElement<Decimal64>, Comparable<Decimal64>
{
    public static final Decimal64 ZERO;
    public static final Decimal64 ONE;
    public static final Decimal64 NEGATIVE_INFINITY;
    public static final Decimal64 POSITIVE_INFINITY;
    public static final Decimal64 NAN;
    private static final long serialVersionUID = 20120227L;
    private final double value;
    
    public Decimal64(final double x) {
        this.value = x;
    }
    
    public Field<Decimal64> getField() {
        return Decimal64Field.getInstance();
    }
    
    public Decimal64 add(final Decimal64 a) {
        return new Decimal64(this.value + a.value);
    }
    
    public Decimal64 subtract(final Decimal64 a) {
        return new Decimal64(this.value - a.value);
    }
    
    public Decimal64 negate() {
        return new Decimal64(-this.value);
    }
    
    public Decimal64 multiply(final Decimal64 a) {
        return new Decimal64(this.value * a.value);
    }
    
    public Decimal64 multiply(final int n) {
        return new Decimal64(n * this.value);
    }
    
    public Decimal64 divide(final Decimal64 a) {
        return new Decimal64(this.value / a.value);
    }
    
    public Decimal64 reciprocal() {
        return new Decimal64(1.0 / this.value);
    }
    
    @Override
    public byte byteValue() {
        return (byte)this.value;
    }
    
    @Override
    public short shortValue() {
        return (short)this.value;
    }
    
    @Override
    public int intValue() {
        return (int)this.value;
    }
    
    @Override
    public long longValue() {
        return (long)this.value;
    }
    
    @Override
    public float floatValue() {
        return (float)this.value;
    }
    
    @Override
    public double doubleValue() {
        return this.value;
    }
    
    public int compareTo(final Decimal64 o) {
        return Double.compare(this.value, o.value);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Decimal64) {
            final Decimal64 that = (Decimal64)obj;
            return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.value);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final long v = Double.doubleToLongBits(this.value);
        return (int)(v ^ v >>> 32);
    }
    
    @Override
    public String toString() {
        return Double.toString(this.value);
    }
    
    public boolean isInfinite() {
        return Double.isInfinite(this.value);
    }
    
    public boolean isNaN() {
        return Double.isNaN(this.value);
    }
    
    public double getReal() {
        return this.value;
    }
    
    public Decimal64 add(final double a) {
        return new Decimal64(this.value + a);
    }
    
    public Decimal64 subtract(final double a) {
        return new Decimal64(this.value - a);
    }
    
    public Decimal64 multiply(final double a) {
        return new Decimal64(this.value * a);
    }
    
    public Decimal64 divide(final double a) {
        return new Decimal64(this.value / a);
    }
    
    public Decimal64 remainder(final double a) {
        return new Decimal64(FastMath.IEEEremainder(this.value, a));
    }
    
    public Decimal64 remainder(final Decimal64 a) {
        return new Decimal64(FastMath.IEEEremainder(this.value, a.value));
    }
    
    public Decimal64 abs() {
        return new Decimal64(FastMath.abs(this.value));
    }
    
    public Decimal64 ceil() {
        return new Decimal64(FastMath.ceil(this.value));
    }
    
    public Decimal64 floor() {
        return new Decimal64(FastMath.floor(this.value));
    }
    
    public Decimal64 rint() {
        return new Decimal64(FastMath.rint(this.value));
    }
    
    public long round() {
        return FastMath.round(this.value);
    }
    
    public Decimal64 signum() {
        return new Decimal64(FastMath.signum(this.value));
    }
    
    public Decimal64 copySign(final Decimal64 sign) {
        return new Decimal64(FastMath.copySign(this.value, sign.value));
    }
    
    public Decimal64 copySign(final double sign) {
        return new Decimal64(FastMath.copySign(this.value, sign));
    }
    
    public Decimal64 scalb(final int n) {
        return new Decimal64(FastMath.scalb(this.value, n));
    }
    
    public Decimal64 hypot(final Decimal64 y) {
        return new Decimal64(FastMath.hypot(this.value, y.value));
    }
    
    public Decimal64 sqrt() {
        return new Decimal64(FastMath.sqrt(this.value));
    }
    
    public Decimal64 cbrt() {
        return new Decimal64(FastMath.cbrt(this.value));
    }
    
    public Decimal64 rootN(final int n) {
        if (this.value < 0.0) {
            return new Decimal64(-FastMath.pow(-this.value, 1.0 / n));
        }
        return new Decimal64(FastMath.pow(this.value, 1.0 / n));
    }
    
    public Decimal64 pow(final double p) {
        return new Decimal64(FastMath.pow(this.value, p));
    }
    
    public Decimal64 pow(final int n) {
        return new Decimal64(FastMath.pow(this.value, n));
    }
    
    public Decimal64 pow(final Decimal64 e) {
        return new Decimal64(FastMath.pow(this.value, e.value));
    }
    
    public Decimal64 exp() {
        return new Decimal64(FastMath.exp(this.value));
    }
    
    public Decimal64 expm1() {
        return new Decimal64(FastMath.expm1(this.value));
    }
    
    public Decimal64 log() {
        return new Decimal64(FastMath.log(this.value));
    }
    
    public Decimal64 log1p() {
        return new Decimal64(FastMath.log1p(this.value));
    }
    
    public Decimal64 log10() {
        return new Decimal64(FastMath.log10(this.value));
    }
    
    public Decimal64 cos() {
        return new Decimal64(FastMath.cos(this.value));
    }
    
    public Decimal64 sin() {
        return new Decimal64(FastMath.sin(this.value));
    }
    
    public Decimal64 tan() {
        return new Decimal64(FastMath.tan(this.value));
    }
    
    public Decimal64 acos() {
        return new Decimal64(FastMath.acos(this.value));
    }
    
    public Decimal64 asin() {
        return new Decimal64(FastMath.asin(this.value));
    }
    
    public Decimal64 atan() {
        return new Decimal64(FastMath.atan(this.value));
    }
    
    public Decimal64 atan2(final Decimal64 x) {
        return new Decimal64(FastMath.atan2(this.value, x.value));
    }
    
    public Decimal64 cosh() {
        return new Decimal64(FastMath.cosh(this.value));
    }
    
    public Decimal64 sinh() {
        return new Decimal64(FastMath.sinh(this.value));
    }
    
    public Decimal64 tanh() {
        return new Decimal64(FastMath.tanh(this.value));
    }
    
    public Decimal64 acosh() {
        return new Decimal64(FastMath.acosh(this.value));
    }
    
    public Decimal64 asinh() {
        return new Decimal64(FastMath.asinh(this.value));
    }
    
    public Decimal64 atanh() {
        return new Decimal64(FastMath.atanh(this.value));
    }
    
    public Decimal64 linearCombination(final Decimal64[] a, final Decimal64[] b) throws DimensionMismatchException {
        if (a.length != b.length) {
            throw new DimensionMismatchException(a.length, b.length);
        }
        final double[] aDouble = new double[a.length];
        final double[] bDouble = new double[b.length];
        for (int i = 0; i < a.length; ++i) {
            aDouble[i] = a[i].value;
            bDouble[i] = b[i].value;
        }
        return new Decimal64(MathArrays.linearCombination(aDouble, bDouble));
    }
    
    public Decimal64 linearCombination(final double[] a, final Decimal64[] b) throws DimensionMismatchException {
        if (a.length != b.length) {
            throw new DimensionMismatchException(a.length, b.length);
        }
        final double[] bDouble = new double[b.length];
        for (int i = 0; i < a.length; ++i) {
            bDouble[i] = b[i].value;
        }
        return new Decimal64(MathArrays.linearCombination(a, bDouble));
    }
    
    public Decimal64 linearCombination(final Decimal64 a1, final Decimal64 b1, final Decimal64 a2, final Decimal64 b2) {
        return new Decimal64(MathArrays.linearCombination(a1.value, b1.value, a2.value, b2.value));
    }
    
    public Decimal64 linearCombination(final double a1, final Decimal64 b1, final double a2, final Decimal64 b2) {
        return new Decimal64(MathArrays.linearCombination(a1, b1.value, a2, b2.value));
    }
    
    public Decimal64 linearCombination(final Decimal64 a1, final Decimal64 b1, final Decimal64 a2, final Decimal64 b2, final Decimal64 a3, final Decimal64 b3) {
        return new Decimal64(MathArrays.linearCombination(a1.value, b1.value, a2.value, b2.value, a3.value, b3.value));
    }
    
    public Decimal64 linearCombination(final double a1, final Decimal64 b1, final double a2, final Decimal64 b2, final double a3, final Decimal64 b3) {
        return new Decimal64(MathArrays.linearCombination(a1, b1.value, a2, b2.value, a3, b3.value));
    }
    
    public Decimal64 linearCombination(final Decimal64 a1, final Decimal64 b1, final Decimal64 a2, final Decimal64 b2, final Decimal64 a3, final Decimal64 b3, final Decimal64 a4, final Decimal64 b4) {
        return new Decimal64(MathArrays.linearCombination(a1.value, b1.value, a2.value, b2.value, a3.value, b3.value, a4.value, b4.value));
    }
    
    public Decimal64 linearCombination(final double a1, final Decimal64 b1, final double a2, final Decimal64 b2, final double a3, final Decimal64 b3, final double a4, final Decimal64 b4) {
        return new Decimal64(MathArrays.linearCombination(a1, b1.value, a2, b2.value, a3, b3.value, a4, b4.value));
    }
    
    static {
        ZERO = new Decimal64(0.0);
        ONE = new Decimal64(1.0);
        NEGATIVE_INFINITY = new Decimal64(Double.NEGATIVE_INFINITY);
        POSITIVE_INFINITY = new Decimal64(Double.POSITIVE_INFINITY);
        NAN = new Decimal64(Double.NaN);
    }
}
