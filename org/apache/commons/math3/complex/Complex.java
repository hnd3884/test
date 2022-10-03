package org.apache.commons.math3.complex;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.Field;
import java.util.ArrayList;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.List;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;
import org.apache.commons.math3.FieldElement;

public class Complex implements FieldElement<Complex>, Serializable
{
    public static final Complex I;
    public static final Complex NaN;
    public static final Complex INF;
    public static final Complex ONE;
    public static final Complex ZERO;
    private static final long serialVersionUID = -6195664516687396620L;
    private final double imaginary;
    private final double real;
    private final transient boolean isNaN;
    private final transient boolean isInfinite;
    
    public Complex(final double real) {
        this(real, 0.0);
    }
    
    public Complex(final double real, final double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
        this.isNaN = (Double.isNaN(real) || Double.isNaN(imaginary));
        this.isInfinite = (!this.isNaN && (Double.isInfinite(real) || Double.isInfinite(imaginary)));
    }
    
    public double abs() {
        if (this.isNaN) {
            return Double.NaN;
        }
        if (this.isInfinite()) {
            return Double.POSITIVE_INFINITY;
        }
        if (FastMath.abs(this.real) < FastMath.abs(this.imaginary)) {
            if (this.imaginary == 0.0) {
                return FastMath.abs(this.real);
            }
            final double q = this.real / this.imaginary;
            return FastMath.abs(this.imaginary) * FastMath.sqrt(1.0 + q * q);
        }
        else {
            if (this.real == 0.0) {
                return FastMath.abs(this.imaginary);
            }
            final double q = this.imaginary / this.real;
            return FastMath.abs(this.real) * FastMath.sqrt(1.0 + q * q);
        }
    }
    
    public Complex add(final Complex addend) throws NullArgumentException {
        MathUtils.checkNotNull(addend);
        if (this.isNaN || addend.isNaN) {
            return Complex.NaN;
        }
        return this.createComplex(this.real + addend.getReal(), this.imaginary + addend.getImaginary());
    }
    
    public Complex add(final double addend) {
        if (this.isNaN || Double.isNaN(addend)) {
            return Complex.NaN;
        }
        return this.createComplex(this.real + addend, this.imaginary);
    }
    
    public Complex conjugate() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        return this.createComplex(this.real, -this.imaginary);
    }
    
    public Complex divide(final Complex divisor) throws NullArgumentException {
        MathUtils.checkNotNull(divisor);
        if (this.isNaN || divisor.isNaN) {
            return Complex.NaN;
        }
        final double c = divisor.getReal();
        final double d = divisor.getImaginary();
        if (c == 0.0 && d == 0.0) {
            return Complex.NaN;
        }
        if (divisor.isInfinite() && !this.isInfinite()) {
            return Complex.ZERO;
        }
        if (FastMath.abs(c) < FastMath.abs(d)) {
            final double q = c / d;
            final double denominator = c * q + d;
            return this.createComplex((this.real * q + this.imaginary) / denominator, (this.imaginary * q - this.real) / denominator);
        }
        final double q = d / c;
        final double denominator = d * q + c;
        return this.createComplex((this.imaginary * q + this.real) / denominator, (this.imaginary - this.real * q) / denominator);
    }
    
    public Complex divide(final double divisor) {
        if (this.isNaN || Double.isNaN(divisor)) {
            return Complex.NaN;
        }
        if (divisor == 0.0) {
            return Complex.NaN;
        }
        if (Double.isInfinite(divisor)) {
            return this.isInfinite() ? Complex.NaN : Complex.ZERO;
        }
        return this.createComplex(this.real / divisor, this.imaginary / divisor);
    }
    
    public Complex reciprocal() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        if (this.real == 0.0 && this.imaginary == 0.0) {
            return Complex.INF;
        }
        if (this.isInfinite) {
            return Complex.ZERO;
        }
        if (FastMath.abs(this.real) < FastMath.abs(this.imaginary)) {
            final double q = this.real / this.imaginary;
            final double scale = 1.0 / (this.real * q + this.imaginary);
            return this.createComplex(scale * q, -scale);
        }
        final double q = this.imaginary / this.real;
        final double scale = 1.0 / (this.imaginary * q + this.real);
        return this.createComplex(scale, -scale * q);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Complex)) {
            return false;
        }
        final Complex c = (Complex)other;
        if (c.isNaN) {
            return this.isNaN;
        }
        return MathUtils.equals(this.real, c.real) && MathUtils.equals(this.imaginary, c.imaginary);
    }
    
    public static boolean equals(final Complex x, final Complex y, final int maxUlps) {
        return Precision.equals(x.real, y.real, maxUlps) && Precision.equals(x.imaginary, y.imaginary, maxUlps);
    }
    
    public static boolean equals(final Complex x, final Complex y) {
        return equals(x, y, 1);
    }
    
    public static boolean equals(final Complex x, final Complex y, final double eps) {
        return Precision.equals(x.real, y.real, eps) && Precision.equals(x.imaginary, y.imaginary, eps);
    }
    
    public static boolean equalsWithRelativeTolerance(final Complex x, final Complex y, final double eps) {
        return Precision.equalsWithRelativeTolerance(x.real, y.real, eps) && Precision.equalsWithRelativeTolerance(x.imaginary, y.imaginary, eps);
    }
    
    @Override
    public int hashCode() {
        if (this.isNaN) {
            return 7;
        }
        return 37 * (17 * MathUtils.hash(this.imaginary) + MathUtils.hash(this.real));
    }
    
    public double getImaginary() {
        return this.imaginary;
    }
    
    public double getReal() {
        return this.real;
    }
    
    public boolean isNaN() {
        return this.isNaN;
    }
    
    public boolean isInfinite() {
        return this.isInfinite;
    }
    
    public Complex multiply(final Complex factor) throws NullArgumentException {
        MathUtils.checkNotNull(factor);
        if (this.isNaN || factor.isNaN) {
            return Complex.NaN;
        }
        if (Double.isInfinite(this.real) || Double.isInfinite(this.imaginary) || Double.isInfinite(factor.real) || Double.isInfinite(factor.imaginary)) {
            return Complex.INF;
        }
        return this.createComplex(this.real * factor.real - this.imaginary * factor.imaginary, this.real * factor.imaginary + this.imaginary * factor.real);
    }
    
    public Complex multiply(final int factor) {
        if (this.isNaN) {
            return Complex.NaN;
        }
        if (Double.isInfinite(this.real) || Double.isInfinite(this.imaginary)) {
            return Complex.INF;
        }
        return this.createComplex(this.real * factor, this.imaginary * factor);
    }
    
    public Complex multiply(final double factor) {
        if (this.isNaN || Double.isNaN(factor)) {
            return Complex.NaN;
        }
        if (Double.isInfinite(this.real) || Double.isInfinite(this.imaginary) || Double.isInfinite(factor)) {
            return Complex.INF;
        }
        return this.createComplex(this.real * factor, this.imaginary * factor);
    }
    
    public Complex negate() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        return this.createComplex(-this.real, -this.imaginary);
    }
    
    public Complex subtract(final Complex subtrahend) throws NullArgumentException {
        MathUtils.checkNotNull(subtrahend);
        if (this.isNaN || subtrahend.isNaN) {
            return Complex.NaN;
        }
        return this.createComplex(this.real - subtrahend.getReal(), this.imaginary - subtrahend.getImaginary());
    }
    
    public Complex subtract(final double subtrahend) {
        if (this.isNaN || Double.isNaN(subtrahend)) {
            return Complex.NaN;
        }
        return this.createComplex(this.real - subtrahend, this.imaginary);
    }
    
    public Complex acos() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        return this.add(this.sqrt1z().multiply(Complex.I)).log().multiply(Complex.I.negate());
    }
    
    public Complex asin() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        return this.sqrt1z().add(this.multiply(Complex.I)).log().multiply(Complex.I.negate());
    }
    
    public Complex atan() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        return this.add(Complex.I).divide(Complex.I.subtract(this)).log().multiply(Complex.I.divide(this.createComplex(2.0, 0.0)));
    }
    
    public Complex cos() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        return this.createComplex(FastMath.cos(this.real) * FastMath.cosh(this.imaginary), -FastMath.sin(this.real) * FastMath.sinh(this.imaginary));
    }
    
    public Complex cosh() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        return this.createComplex(FastMath.cosh(this.real) * FastMath.cos(this.imaginary), FastMath.sinh(this.real) * FastMath.sin(this.imaginary));
    }
    
    public Complex exp() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        final double expReal = FastMath.exp(this.real);
        return this.createComplex(expReal * FastMath.cos(this.imaginary), expReal * FastMath.sin(this.imaginary));
    }
    
    public Complex log() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        return this.createComplex(FastMath.log(this.abs()), FastMath.atan2(this.imaginary, this.real));
    }
    
    public Complex pow(final Complex x) throws NullArgumentException {
        MathUtils.checkNotNull(x);
        return this.log().multiply(x).exp();
    }
    
    public Complex pow(final double x) {
        return this.log().multiply(x).exp();
    }
    
    public Complex sin() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        return this.createComplex(FastMath.sin(this.real) * FastMath.cosh(this.imaginary), FastMath.cos(this.real) * FastMath.sinh(this.imaginary));
    }
    
    public Complex sinh() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        return this.createComplex(FastMath.sinh(this.real) * FastMath.cos(this.imaginary), FastMath.cosh(this.real) * FastMath.sin(this.imaginary));
    }
    
    public Complex sqrt() {
        if (this.isNaN) {
            return Complex.NaN;
        }
        if (this.real == 0.0 && this.imaginary == 0.0) {
            return this.createComplex(0.0, 0.0);
        }
        final double t = FastMath.sqrt((FastMath.abs(this.real) + this.abs()) / 2.0);
        if (this.real >= 0.0) {
            return this.createComplex(t, this.imaginary / (2.0 * t));
        }
        return this.createComplex(FastMath.abs(this.imaginary) / (2.0 * t), FastMath.copySign(1.0, this.imaginary) * t);
    }
    
    public Complex sqrt1z() {
        return this.createComplex(1.0, 0.0).subtract(this.multiply(this)).sqrt();
    }
    
    public Complex tan() {
        if (this.isNaN || Double.isInfinite(this.real)) {
            return Complex.NaN;
        }
        if (this.imaginary > 20.0) {
            return this.createComplex(0.0, 1.0);
        }
        if (this.imaginary < -20.0) {
            return this.createComplex(0.0, -1.0);
        }
        final double real2 = 2.0 * this.real;
        final double imaginary2 = 2.0 * this.imaginary;
        final double d = FastMath.cos(real2) + FastMath.cosh(imaginary2);
        return this.createComplex(FastMath.sin(real2) / d, FastMath.sinh(imaginary2) / d);
    }
    
    public Complex tanh() {
        if (this.isNaN || Double.isInfinite(this.imaginary)) {
            return Complex.NaN;
        }
        if (this.real > 20.0) {
            return this.createComplex(1.0, 0.0);
        }
        if (this.real < -20.0) {
            return this.createComplex(-1.0, 0.0);
        }
        final double real2 = 2.0 * this.real;
        final double imaginary2 = 2.0 * this.imaginary;
        final double d = FastMath.cosh(real2) + FastMath.cos(imaginary2);
        return this.createComplex(FastMath.sinh(real2) / d, FastMath.sin(imaginary2) / d);
    }
    
    public double getArgument() {
        return FastMath.atan2(this.getImaginary(), this.getReal());
    }
    
    public List<Complex> nthRoot(final int n) throws NotPositiveException {
        if (n <= 0) {
            throw new NotPositiveException(LocalizedFormats.CANNOT_COMPUTE_NTH_ROOT_FOR_NEGATIVE_N, n);
        }
        final List<Complex> result = new ArrayList<Complex>();
        if (this.isNaN) {
            result.add(Complex.NaN);
            return result;
        }
        if (this.isInfinite()) {
            result.add(Complex.INF);
            return result;
        }
        final double nthRootOfAbs = FastMath.pow(this.abs(), 1.0 / n);
        final double nthPhi = this.getArgument() / n;
        final double slice = 6.283185307179586 / n;
        double innerPart = nthPhi;
        for (int k = 0; k < n; ++k) {
            final double realPart = nthRootOfAbs * FastMath.cos(innerPart);
            final double imaginaryPart = nthRootOfAbs * FastMath.sin(innerPart);
            result.add(this.createComplex(realPart, imaginaryPart));
            innerPart += slice;
        }
        return result;
    }
    
    protected Complex createComplex(final double realPart, final double imaginaryPart) {
        return new Complex(realPart, imaginaryPart);
    }
    
    public static Complex valueOf(final double realPart, final double imaginaryPart) {
        if (Double.isNaN(realPart) || Double.isNaN(imaginaryPart)) {
            return Complex.NaN;
        }
        return new Complex(realPart, imaginaryPart);
    }
    
    public static Complex valueOf(final double realPart) {
        if (Double.isNaN(realPart)) {
            return Complex.NaN;
        }
        return new Complex(realPart);
    }
    
    protected final Object readResolve() {
        return this.createComplex(this.real, this.imaginary);
    }
    
    public ComplexField getField() {
        return ComplexField.getInstance();
    }
    
    @Override
    public String toString() {
        return "(" + this.real + ", " + this.imaginary + ")";
    }
    
    static {
        I = new Complex(0.0, 1.0);
        NaN = new Complex(Double.NaN, Double.NaN);
        INF = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        ONE = new Complex(1.0, 0.0);
        ZERO = new Complex(0.0, 0.0);
    }
}
