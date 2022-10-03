package org.apache.commons.math3.fraction;

import org.apache.commons.math3.Field;
import java.math.BigInteger;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;
import org.apache.commons.math3.FieldElement;

public class Fraction extends Number implements FieldElement<Fraction>, Comparable<Fraction>, Serializable
{
    public static final Fraction TWO;
    public static final Fraction ONE;
    public static final Fraction ZERO;
    public static final Fraction FOUR_FIFTHS;
    public static final Fraction ONE_FIFTH;
    public static final Fraction ONE_HALF;
    public static final Fraction ONE_QUARTER;
    public static final Fraction ONE_THIRD;
    public static final Fraction THREE_FIFTHS;
    public static final Fraction THREE_QUARTERS;
    public static final Fraction TWO_FIFTHS;
    public static final Fraction TWO_QUARTERS;
    public static final Fraction TWO_THIRDS;
    public static final Fraction MINUS_ONE;
    private static final long serialVersionUID = 3698073679419233275L;
    private static final double DEFAULT_EPSILON = 1.0E-5;
    private final int denominator;
    private final int numerator;
    
    public Fraction(final double value) throws FractionConversionException {
        this(value, 1.0E-5, 100);
    }
    
    public Fraction(final double value, final double epsilon, final int maxIterations) throws FractionConversionException {
        this(value, epsilon, Integer.MAX_VALUE, maxIterations);
    }
    
    public Fraction(final double value, final int maxDenominator) throws FractionConversionException {
        this(value, 0.0, maxDenominator, 100);
    }
    
    private Fraction(final double value, final double epsilon, final int maxDenominator, final int maxIterations) throws FractionConversionException {
        final long overflow = 2147483647L;
        double r0 = value;
        long a0 = (long)FastMath.floor(r0);
        if (FastMath.abs(a0) > overflow) {
            throw new FractionConversionException(value, a0, 1L);
        }
        if (FastMath.abs(a0 - value) < epsilon) {
            this.numerator = (int)a0;
            this.denominator = 1;
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
            if (FastMath.abs(p3) > overflow || FastMath.abs(q3) > overflow) {
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
            this.numerator = (int)p3;
            this.denominator = (int)q3;
        }
        else {
            this.numerator = (int)p2;
            this.denominator = (int)q2;
        }
    }
    
    public Fraction(final int num) {
        this(num, 1);
    }
    
    public Fraction(int num, int den) {
        if (den == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR_IN_FRACTION, new Object[] { num, den });
        }
        if (den < 0) {
            if (num == Integer.MIN_VALUE || den == Integer.MIN_VALUE) {
                throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, new Object[] { num, den });
            }
            num = -num;
            den = -den;
        }
        final int d = ArithmeticUtils.gcd(num, den);
        if (d > 1) {
            num /= d;
            den /= d;
        }
        if (den < 0) {
            num = -num;
            den = -den;
        }
        this.numerator = num;
        this.denominator = den;
    }
    
    public Fraction abs() {
        Fraction ret;
        if (this.numerator >= 0) {
            ret = this;
        }
        else {
            ret = this.negate();
        }
        return ret;
    }
    
    public int compareTo(final Fraction object) {
        final long nOd = this.numerator * (long)object.denominator;
        final long dOn = this.denominator * (long)object.numerator;
        return (nOd < dOn) ? -1 : ((nOd > dOn) ? 1 : 0);
    }
    
    @Override
    public double doubleValue() {
        return this.numerator / (double)this.denominator;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Fraction) {
            final Fraction rhs = (Fraction)other;
            return this.numerator == rhs.numerator && this.denominator == rhs.denominator;
        }
        return false;
    }
    
    @Override
    public float floatValue() {
        return (float)this.doubleValue();
    }
    
    public int getDenominator() {
        return this.denominator;
    }
    
    public int getNumerator() {
        return this.numerator;
    }
    
    @Override
    public int hashCode() {
        return 37 * (629 + this.numerator) + this.denominator;
    }
    
    @Override
    public int intValue() {
        return (int)this.doubleValue();
    }
    
    @Override
    public long longValue() {
        return (long)this.doubleValue();
    }
    
    public Fraction negate() {
        if (this.numerator == Integer.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, new Object[] { this.numerator, this.denominator });
        }
        return new Fraction(-this.numerator, this.denominator);
    }
    
    public Fraction reciprocal() {
        return new Fraction(this.denominator, this.numerator);
    }
    
    public Fraction add(final Fraction fraction) {
        return this.addSub(fraction, true);
    }
    
    public Fraction add(final int i) {
        return new Fraction(this.numerator + i * this.denominator, this.denominator);
    }
    
    public Fraction subtract(final Fraction fraction) {
        return this.addSub(fraction, false);
    }
    
    public Fraction subtract(final int i) {
        return new Fraction(this.numerator - i * this.denominator, this.denominator);
    }
    
    private Fraction addSub(final Fraction fraction, final boolean isAdd) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        }
        if (this.numerator == 0) {
            return isAdd ? fraction : fraction.negate();
        }
        if (fraction.numerator == 0) {
            return this;
        }
        final int d1 = ArithmeticUtils.gcd(this.denominator, fraction.denominator);
        if (d1 == 1) {
            final int uvp = ArithmeticUtils.mulAndCheck(this.numerator, fraction.denominator);
            final int upv = ArithmeticUtils.mulAndCheck(fraction.numerator, this.denominator);
            return new Fraction(isAdd ? ArithmeticUtils.addAndCheck(uvp, upv) : ArithmeticUtils.subAndCheck(uvp, upv), ArithmeticUtils.mulAndCheck(this.denominator, fraction.denominator));
        }
        final BigInteger uvp2 = BigInteger.valueOf(this.numerator).multiply(BigInteger.valueOf(fraction.denominator / d1));
        final BigInteger upv2 = BigInteger.valueOf(fraction.numerator).multiply(BigInteger.valueOf(this.denominator / d1));
        final BigInteger t = isAdd ? uvp2.add(upv2) : uvp2.subtract(upv2);
        final int tmodd1 = t.mod(BigInteger.valueOf(d1)).intValue();
        final int d2 = (tmodd1 == 0) ? d1 : ArithmeticUtils.gcd(tmodd1, d1);
        final BigInteger w = t.divide(BigInteger.valueOf(d2));
        if (w.bitLength() > 31) {
            throw new MathArithmeticException(LocalizedFormats.NUMERATOR_OVERFLOW_AFTER_MULTIPLY, new Object[] { w });
        }
        return new Fraction(w.intValue(), ArithmeticUtils.mulAndCheck(this.denominator / d1, fraction.denominator / d2));
    }
    
    public Fraction multiply(final Fraction fraction) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        }
        if (this.numerator == 0 || fraction.numerator == 0) {
            return Fraction.ZERO;
        }
        final int d1 = ArithmeticUtils.gcd(this.numerator, fraction.denominator);
        final int d2 = ArithmeticUtils.gcd(fraction.numerator, this.denominator);
        return getReducedFraction(ArithmeticUtils.mulAndCheck(this.numerator / d1, fraction.numerator / d2), ArithmeticUtils.mulAndCheck(this.denominator / d2, fraction.denominator / d1));
    }
    
    public Fraction multiply(final int i) {
        return this.multiply(new Fraction(i));
    }
    
    public Fraction divide(final Fraction fraction) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        }
        if (fraction.numerator == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_FRACTION_TO_DIVIDE_BY, new Object[] { fraction.numerator, fraction.denominator });
        }
        return this.multiply(fraction.reciprocal());
    }
    
    public Fraction divide(final int i) {
        return this.divide(new Fraction(i));
    }
    
    public double percentageValue() {
        return 100.0 * this.doubleValue();
    }
    
    public static Fraction getReducedFraction(int numerator, int denominator) {
        if (denominator == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR_IN_FRACTION, new Object[] { numerator, denominator });
        }
        if (numerator == 0) {
            return Fraction.ZERO;
        }
        if (denominator == Integer.MIN_VALUE && (numerator & 0x1) == 0x0) {
            numerator /= 2;
            denominator /= 2;
        }
        if (denominator < 0) {
            if (numerator == Integer.MIN_VALUE || denominator == Integer.MIN_VALUE) {
                throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, new Object[] { numerator, denominator });
            }
            numerator = -numerator;
            denominator = -denominator;
        }
        final int gcd = ArithmeticUtils.gcd(numerator, denominator);
        numerator /= gcd;
        denominator /= gcd;
        return new Fraction(numerator, denominator);
    }
    
    @Override
    public String toString() {
        String str = null;
        if (this.denominator == 1) {
            str = Integer.toString(this.numerator);
        }
        else if (this.numerator == 0) {
            str = "0";
        }
        else {
            str = this.numerator + " / " + this.denominator;
        }
        return str;
    }
    
    public FractionField getField() {
        return FractionField.getInstance();
    }
    
    static {
        TWO = new Fraction(2, 1);
        ONE = new Fraction(1, 1);
        ZERO = new Fraction(0, 1);
        FOUR_FIFTHS = new Fraction(4, 5);
        ONE_FIFTH = new Fraction(1, 5);
        ONE_HALF = new Fraction(1, 2);
        ONE_QUARTER = new Fraction(1, 4);
        ONE_THIRD = new Fraction(1, 3);
        THREE_FIFTHS = new Fraction(3, 5);
        THREE_QUARTERS = new Fraction(3, 4);
        TWO_FIFTHS = new Fraction(2, 5);
        TWO_QUARTERS = new Fraction(2, 4);
        TWO_THIRDS = new Fraction(2, 3);
        MINUS_ONE = new Fraction(-1, 1);
    }
}
