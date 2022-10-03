package org.apache.commons.math3.fraction;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.exception.MathArithmeticException;
import java.math.BigDecimal;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.math.BigInteger;
import java.io.Serializable;
import org.apache.commons.math3.FieldElement;

public class BigFraction extends Number implements FieldElement<BigFraction>, Comparable<BigFraction>, Serializable
{
    public static final BigFraction TWO;
    public static final BigFraction ONE;
    public static final BigFraction ZERO;
    public static final BigFraction MINUS_ONE;
    public static final BigFraction FOUR_FIFTHS;
    public static final BigFraction ONE_FIFTH;
    public static final BigFraction ONE_HALF;
    public static final BigFraction ONE_QUARTER;
    public static final BigFraction ONE_THIRD;
    public static final BigFraction THREE_FIFTHS;
    public static final BigFraction THREE_QUARTERS;
    public static final BigFraction TWO_FIFTHS;
    public static final BigFraction TWO_QUARTERS;
    public static final BigFraction TWO_THIRDS;
    private static final long serialVersionUID = -5630213147331578515L;
    private static final BigInteger ONE_HUNDRED;
    private final BigInteger numerator;
    private final BigInteger denominator;
    
    public BigFraction(final BigInteger num) {
        this(num, BigInteger.ONE);
    }
    
    public BigFraction(BigInteger num, BigInteger den) {
        MathUtils.checkNotNull(num, LocalizedFormats.NUMERATOR, new Object[0]);
        MathUtils.checkNotNull(den, LocalizedFormats.DENOMINATOR, new Object[0]);
        if (den.signum() == 0) {
            throw new ZeroException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        if (num.signum() == 0) {
            this.numerator = BigInteger.ZERO;
            this.denominator = BigInteger.ONE;
        }
        else {
            final BigInteger gcd = num.gcd(den);
            if (BigInteger.ONE.compareTo(gcd) < 0) {
                num = num.divide(gcd);
                den = den.divide(gcd);
            }
            if (den.signum() == -1) {
                num = num.negate();
                den = den.negate();
            }
            this.numerator = num;
            this.denominator = den;
        }
    }
    
    public BigFraction(final double value) throws MathIllegalArgumentException {
        if (Double.isNaN(value)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NAN_VALUE_CONVERSION, new Object[0]);
        }
        if (Double.isInfinite(value)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INFINITE_VALUE_CONVERSION, new Object[0]);
        }
        final long bits = Double.doubleToLongBits(value);
        final long sign = bits & Long.MIN_VALUE;
        final long exponent = bits & 0x7FF0000000000000L;
        long m = bits & 0xFFFFFFFFFFFFFL;
        if (exponent != 0L) {
            m |= 0x10000000000000L;
        }
        if (sign != 0L) {
            m = -m;
        }
        int k;
        for (k = (int)(exponent >> 52) - 1075; (m & 0x1FFFFFFFFFFFFEL) != 0x0L && (m & 0x1L) == 0x0L; m >>= 1, ++k) {}
        if (k < 0) {
            this.numerator = BigInteger.valueOf(m);
            this.denominator = BigInteger.ZERO.flipBit(-k);
        }
        else {
            this.numerator = BigInteger.valueOf(m).multiply(BigInteger.ZERO.flipBit(k));
            this.denominator = BigInteger.ONE;
        }
    }
    
    public BigFraction(final double value, final double epsilon, final int maxIterations) throws FractionConversionException {
        this(value, epsilon, Integer.MAX_VALUE, maxIterations);
    }
    
    private BigFraction(final double value, final double epsilon, final int maxDenominator, final int maxIterations) throws FractionConversionException {
        final long overflow = 2147483647L;
        double r0 = value;
        long a0 = (long)FastMath.floor(r0);
        if (FastMath.abs(a0) > overflow) {
            throw new FractionConversionException(value, a0, 1L);
        }
        if (FastMath.abs(a0 - value) < epsilon) {
            this.numerator = BigInteger.valueOf(a0);
            this.denominator = BigInteger.ONE;
            return;
        }
        long p0 = 1L;
        long q0 = 0L;
        long p2 = a0;
        long q2 = 1L;
        long p3 = 0L;
        long q3 = 1L;
        int n = 0;
        boolean stop = false;
        do {
            ++n;
            final double r2 = 1.0 / (r0 - a0);
            final long a2 = (long)FastMath.floor(r2);
            p3 = a2 * p2 + p0;
            q3 = a2 * q2 + q0;
            if (p3 > overflow || q3 > overflow) {
                if (epsilon == 0.0 && FastMath.abs(q2) < maxDenominator) {
                    break;
                }
                throw new FractionConversionException(value, p3, q3);
            }
            else {
                final double convergent = p3 / (double)q3;
                if (n < maxIterations && FastMath.abs(convergent - value) > epsilon && q3 < maxDenominator) {
                    p0 = p2;
                    p2 = p3;
                    q0 = q2;
                    q2 = q3;
                    a0 = a2;
                    r0 = r2;
                }
                else {
                    stop = true;
                }
            }
        } while (!stop);
        if (n >= maxIterations) {
            throw new FractionConversionException(value, maxIterations);
        }
        if (q3 < maxDenominator) {
            this.numerator = BigInteger.valueOf(p3);
            this.denominator = BigInteger.valueOf(q3);
        }
        else {
            this.numerator = BigInteger.valueOf(p2);
            this.denominator = BigInteger.valueOf(q2);
        }
    }
    
    public BigFraction(final double value, final int maxDenominator) throws FractionConversionException {
        this(value, 0.0, maxDenominator, 100);
    }
    
    public BigFraction(final int num) {
        this(BigInteger.valueOf(num), BigInteger.ONE);
    }
    
    public BigFraction(final int num, final int den) {
        this(BigInteger.valueOf(num), BigInteger.valueOf(den));
    }
    
    public BigFraction(final long num) {
        this(BigInteger.valueOf(num), BigInteger.ONE);
    }
    
    public BigFraction(final long num, final long den) {
        this(BigInteger.valueOf(num), BigInteger.valueOf(den));
    }
    
    public static BigFraction getReducedFraction(final int numerator, final int denominator) {
        if (numerator == 0) {
            return BigFraction.ZERO;
        }
        return new BigFraction(numerator, denominator);
    }
    
    public BigFraction abs() {
        return (this.numerator.signum() == 1) ? this : this.negate();
    }
    
    public BigFraction add(final BigInteger bg) throws NullArgumentException {
        MathUtils.checkNotNull(bg);
        if (this.numerator.signum() == 0) {
            return new BigFraction(bg);
        }
        if (bg.signum() == 0) {
            return this;
        }
        return new BigFraction(this.numerator.add(this.denominator.multiply(bg)), this.denominator);
    }
    
    public BigFraction add(final int i) {
        return this.add(BigInteger.valueOf(i));
    }
    
    public BigFraction add(final long l) {
        return this.add(BigInteger.valueOf(l));
    }
    
    public BigFraction add(final BigFraction fraction) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        }
        if (fraction.numerator.signum() == 0) {
            return this;
        }
        if (this.numerator.signum() == 0) {
            return fraction;
        }
        BigInteger num = null;
        BigInteger den = null;
        if (this.denominator.equals(fraction.denominator)) {
            num = this.numerator.add(fraction.numerator);
            den = this.denominator;
        }
        else {
            num = this.numerator.multiply(fraction.denominator).add(fraction.numerator.multiply(this.denominator));
            den = this.denominator.multiply(fraction.denominator);
        }
        if (num.signum() == 0) {
            return BigFraction.ZERO;
        }
        return new BigFraction(num, den);
    }
    
    public BigDecimal bigDecimalValue() {
        return new BigDecimal(this.numerator).divide(new BigDecimal(this.denominator));
    }
    
    public BigDecimal bigDecimalValue(final int roundingMode) {
        return new BigDecimal(this.numerator).divide(new BigDecimal(this.denominator), roundingMode);
    }
    
    public BigDecimal bigDecimalValue(final int scale, final int roundingMode) {
        return new BigDecimal(this.numerator).divide(new BigDecimal(this.denominator), scale, roundingMode);
    }
    
    public int compareTo(final BigFraction object) {
        final int lhsSigNum = this.numerator.signum();
        final int rhsSigNum = object.numerator.signum();
        if (lhsSigNum != rhsSigNum) {
            return (lhsSigNum > rhsSigNum) ? 1 : -1;
        }
        if (lhsSigNum == 0) {
            return 0;
        }
        final BigInteger nOd = this.numerator.multiply(object.denominator);
        final BigInteger dOn = this.denominator.multiply(object.numerator);
        return nOd.compareTo(dOn);
    }
    
    public BigFraction divide(final BigInteger bg) {
        if (bg == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        }
        if (bg.signum() == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        if (this.numerator.signum() == 0) {
            return BigFraction.ZERO;
        }
        return new BigFraction(this.numerator, this.denominator.multiply(bg));
    }
    
    public BigFraction divide(final int i) {
        return this.divide(BigInteger.valueOf(i));
    }
    
    public BigFraction divide(final long l) {
        return this.divide(BigInteger.valueOf(l));
    }
    
    public BigFraction divide(final BigFraction fraction) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        }
        if (fraction.numerator.signum() == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        if (this.numerator.signum() == 0) {
            return BigFraction.ZERO;
        }
        return this.multiply(fraction.reciprocal());
    }
    
    @Override
    public double doubleValue() {
        double result = this.numerator.doubleValue() / this.denominator.doubleValue();
        if (Double.isNaN(result)) {
            final int shift = FastMath.max(this.numerator.bitLength(), this.denominator.bitLength()) - FastMath.getExponent(Double.MAX_VALUE);
            result = this.numerator.shiftRight(shift).doubleValue() / this.denominator.shiftRight(shift).doubleValue();
        }
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        boolean ret = false;
        if (this == other) {
            ret = true;
        }
        else if (other instanceof BigFraction) {
            final BigFraction rhs = ((BigFraction)other).reduce();
            final BigFraction thisOne = this.reduce();
            ret = (thisOne.numerator.equals(rhs.numerator) && thisOne.denominator.equals(rhs.denominator));
        }
        return ret;
    }
    
    @Override
    public float floatValue() {
        float result = this.numerator.floatValue() / this.denominator.floatValue();
        if (Double.isNaN(result)) {
            final int shift = FastMath.max(this.numerator.bitLength(), this.denominator.bitLength()) - FastMath.getExponent(Float.MAX_VALUE);
            result = this.numerator.shiftRight(shift).floatValue() / this.denominator.shiftRight(shift).floatValue();
        }
        return result;
    }
    
    public BigInteger getDenominator() {
        return this.denominator;
    }
    
    public int getDenominatorAsInt() {
        return this.denominator.intValue();
    }
    
    public long getDenominatorAsLong() {
        return this.denominator.longValue();
    }
    
    public BigInteger getNumerator() {
        return this.numerator;
    }
    
    public int getNumeratorAsInt() {
        return this.numerator.intValue();
    }
    
    public long getNumeratorAsLong() {
        return this.numerator.longValue();
    }
    
    @Override
    public int hashCode() {
        return 37 * (629 + this.numerator.hashCode()) + this.denominator.hashCode();
    }
    
    @Override
    public int intValue() {
        return this.numerator.divide(this.denominator).intValue();
    }
    
    @Override
    public long longValue() {
        return this.numerator.divide(this.denominator).longValue();
    }
    
    public BigFraction multiply(final BigInteger bg) {
        if (bg == null) {
            throw new NullArgumentException();
        }
        if (this.numerator.signum() == 0 || bg.signum() == 0) {
            return BigFraction.ZERO;
        }
        return new BigFraction(bg.multiply(this.numerator), this.denominator);
    }
    
    public BigFraction multiply(final int i) {
        if (i == 0 || this.numerator.signum() == 0) {
            return BigFraction.ZERO;
        }
        return this.multiply(BigInteger.valueOf(i));
    }
    
    public BigFraction multiply(final long l) {
        if (l == 0L || this.numerator.signum() == 0) {
            return BigFraction.ZERO;
        }
        return this.multiply(BigInteger.valueOf(l));
    }
    
    public BigFraction multiply(final BigFraction fraction) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        }
        if (this.numerator.signum() == 0 || fraction.numerator.signum() == 0) {
            return BigFraction.ZERO;
        }
        return new BigFraction(this.numerator.multiply(fraction.numerator), this.denominator.multiply(fraction.denominator));
    }
    
    public BigFraction negate() {
        return new BigFraction(this.numerator.negate(), this.denominator);
    }
    
    public double percentageValue() {
        return this.multiply(BigFraction.ONE_HUNDRED).doubleValue();
    }
    
    public BigFraction pow(final int exponent) {
        if (exponent == 0) {
            return BigFraction.ONE;
        }
        if (this.numerator.signum() == 0) {
            return this;
        }
        if (exponent < 0) {
            return new BigFraction(this.denominator.pow(-exponent), this.numerator.pow(-exponent));
        }
        return new BigFraction(this.numerator.pow(exponent), this.denominator.pow(exponent));
    }
    
    public BigFraction pow(final long exponent) {
        if (exponent == 0L) {
            return BigFraction.ONE;
        }
        if (this.numerator.signum() == 0) {
            return this;
        }
        if (exponent < 0L) {
            return new BigFraction(ArithmeticUtils.pow(this.denominator, -exponent), ArithmeticUtils.pow(this.numerator, -exponent));
        }
        return new BigFraction(ArithmeticUtils.pow(this.numerator, exponent), ArithmeticUtils.pow(this.denominator, exponent));
    }
    
    public BigFraction pow(final BigInteger exponent) {
        if (exponent.signum() == 0) {
            return BigFraction.ONE;
        }
        if (this.numerator.signum() == 0) {
            return this;
        }
        if (exponent.signum() == -1) {
            final BigInteger eNeg = exponent.negate();
            return new BigFraction(ArithmeticUtils.pow(this.denominator, eNeg), ArithmeticUtils.pow(this.numerator, eNeg));
        }
        return new BigFraction(ArithmeticUtils.pow(this.numerator, exponent), ArithmeticUtils.pow(this.denominator, exponent));
    }
    
    public double pow(final double exponent) {
        return FastMath.pow(this.numerator.doubleValue(), exponent) / FastMath.pow(this.denominator.doubleValue(), exponent);
    }
    
    public BigFraction reciprocal() {
        return new BigFraction(this.denominator, this.numerator);
    }
    
    public BigFraction reduce() {
        final BigInteger gcd = this.numerator.gcd(this.denominator);
        if (BigInteger.ONE.compareTo(gcd) < 0) {
            return new BigFraction(this.numerator.divide(gcd), this.denominator.divide(gcd));
        }
        return this;
    }
    
    public BigFraction subtract(final BigInteger bg) {
        if (bg == null) {
            throw new NullArgumentException();
        }
        if (bg.signum() == 0) {
            return this;
        }
        if (this.numerator.signum() == 0) {
            return new BigFraction(bg.negate());
        }
        return new BigFraction(this.numerator.subtract(this.denominator.multiply(bg)), this.denominator);
    }
    
    public BigFraction subtract(final int i) {
        return this.subtract(BigInteger.valueOf(i));
    }
    
    public BigFraction subtract(final long l) {
        return this.subtract(BigInteger.valueOf(l));
    }
    
    public BigFraction subtract(final BigFraction fraction) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        }
        if (fraction.numerator.signum() == 0) {
            return this;
        }
        if (this.numerator.signum() == 0) {
            return fraction.negate();
        }
        BigInteger num = null;
        BigInteger den = null;
        if (this.denominator.equals(fraction.denominator)) {
            num = this.numerator.subtract(fraction.numerator);
            den = this.denominator;
        }
        else {
            num = this.numerator.multiply(fraction.denominator).subtract(fraction.numerator.multiply(this.denominator));
            den = this.denominator.multiply(fraction.denominator);
        }
        return new BigFraction(num, den);
    }
    
    @Override
    public String toString() {
        String str = null;
        if (BigInteger.ONE.equals(this.denominator)) {
            str = this.numerator.toString();
        }
        else if (BigInteger.ZERO.equals(this.numerator)) {
            str = "0";
        }
        else {
            str = this.numerator + " / " + this.denominator;
        }
        return str;
    }
    
    public BigFractionField getField() {
        return BigFractionField.getInstance();
    }
    
    static {
        TWO = new BigFraction(2);
        ONE = new BigFraction(1);
        ZERO = new BigFraction(0);
        MINUS_ONE = new BigFraction(-1);
        FOUR_FIFTHS = new BigFraction(4, 5);
        ONE_FIFTH = new BigFraction(1, 5);
        ONE_HALF = new BigFraction(1, 2);
        ONE_QUARTER = new BigFraction(1, 4);
        ONE_THIRD = new BigFraction(1, 3);
        THREE_FIFTHS = new BigFraction(3, 5);
        THREE_QUARTERS = new BigFraction(3, 4);
        TWO_FIFTHS = new BigFraction(2, 5);
        TWO_QUARTERS = new BigFraction(2, 4);
        TWO_THIRDS = new BigFraction(2, 3);
        ONE_HUNDRED = BigInteger.valueOf(100L);
    }
}
