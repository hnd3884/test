package org.apache.commons.lang.math;

import java.io.Serializable;

public final class Fraction extends Number implements Serializable, Comparable
{
    private static final long serialVersionUID = 65382027393090L;
    public static final Fraction ZERO;
    public static final Fraction ONE;
    public static final Fraction ONE_HALF;
    public static final Fraction ONE_THIRD;
    public static final Fraction TWO_THIRDS;
    public static final Fraction ONE_QUARTER;
    public static final Fraction TWO_QUARTERS;
    public static final Fraction THREE_QUARTERS;
    public static final Fraction ONE_FIFTH;
    public static final Fraction TWO_FIFTHS;
    public static final Fraction THREE_FIFTHS;
    public static final Fraction FOUR_FIFTHS;
    private final int numerator;
    private final int denominator;
    private transient int hashCode;
    private transient String toString;
    private transient String toProperString;
    
    private Fraction(final int numerator, final int denominator) {
        this.hashCode = 0;
        this.toString = null;
        this.toProperString = null;
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    public static Fraction getFraction(int numerator, int denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("The denominator must not be zero");
        }
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        return new Fraction(numerator, denominator);
    }
    
    public static Fraction getFraction(final int whole, final int numerator, final int denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("The denominator must not be zero");
        }
        if (denominator < 0) {
            throw new ArithmeticException("The denominator must not be negative");
        }
        if (numerator < 0) {
            throw new ArithmeticException("The numerator must not be negative");
        }
        double numeratorValue = 0.0;
        if (whole < 0) {
            numeratorValue = whole * (double)denominator - numerator;
        }
        else {
            numeratorValue = whole * (double)denominator + numerator;
        }
        if (Math.abs(numeratorValue) > 2.147483647E9) {
            throw new ArithmeticException("Numerator too large to represent as an Integer.");
        }
        return new Fraction((int)numeratorValue, denominator);
    }
    
    public static Fraction getReducedFraction(int numerator, int denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("The denominator must not be zero");
        }
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        final int gcd = greatestCommonDivisor(Math.abs(numerator), denominator);
        return new Fraction(numerator / gcd, denominator / gcd);
    }
    
    public static Fraction getFraction(double value) {
        final int sign = (value < 0.0) ? -1 : 1;
        value = Math.abs(value);
        if (value > 2.147483647E9 || Double.isNaN(value)) {
            throw new ArithmeticException("The value must not be greater than Integer.MAX_VALUE or NaN");
        }
        final int wholeNumber = (int)value;
        value -= wholeNumber;
        int numer0 = 0;
        int denom0 = 1;
        int numer2 = 1;
        int denom2 = 0;
        int numer3 = 0;
        int denom3 = 0;
        int a1 = (int)value;
        int a2 = 0;
        double x1 = 1.0;
        double x2 = 0.0;
        double y1 = value - a1;
        double y2 = 0.0;
        double delta2 = Double.MAX_VALUE;
        int i = 1;
        double delta3;
        do {
            delta3 = delta2;
            a2 = (int)(x1 / y1);
            x2 = y1;
            y2 = x1 - a2 * y1;
            numer3 = a1 * numer2 + numer0;
            denom3 = a1 * denom2 + denom0;
            final double fraction = numer3 / (double)denom3;
            delta2 = Math.abs(value - fraction);
            a1 = a2;
            x1 = x2;
            y1 = y2;
            numer0 = numer2;
            denom0 = denom2;
            numer2 = numer3;
            denom2 = denom3;
            ++i;
        } while (delta3 > delta2 && denom3 <= 10000 && denom3 > 0 && i < 25);
        if (i == 25) {
            throw new ArithmeticException("Unable to convert double to fraction");
        }
        return getReducedFraction((numer0 + wholeNumber * denom0) * sign, denom0);
    }
    
    public static Fraction getFraction(String str) {
        if (str == null) {
            throw new IllegalArgumentException("The string must not be null");
        }
        int pos = str.indexOf(46);
        if (pos >= 0) {
            return getFraction(Double.parseDouble(str));
        }
        pos = str.indexOf(32);
        if (pos > 0) {
            final int whole = Integer.parseInt(str.substring(0, pos));
            str = str.substring(pos + 1);
            pos = str.indexOf(47);
            if (pos < 0) {
                throw new NumberFormatException("The fraction could not be parsed as the format X Y/Z");
            }
            final int denom = Integer.parseInt(str.substring(pos + 1));
            return getFraction(Integer.parseInt(str.substring(0, pos)) + whole * denom, denom);
        }
        else {
            pos = str.indexOf(47);
            if (pos < 0) {
                return getFraction(Integer.parseInt(str), 1);
            }
            return getFraction(Integer.parseInt(str.substring(0, pos)), Integer.parseInt(str.substring(pos + 1)));
        }
    }
    
    public int getNumerator() {
        return this.numerator;
    }
    
    public int getDenominator() {
        return this.denominator;
    }
    
    public int getProperNumerator() {
        return Math.abs(this.numerator % this.denominator);
    }
    
    public int getProperWhole() {
        return this.numerator / this.denominator;
    }
    
    public int intValue() {
        return this.numerator / this.denominator;
    }
    
    public long longValue() {
        return this.numerator / (long)this.denominator;
    }
    
    public float floatValue() {
        return this.numerator / (float)this.denominator;
    }
    
    public double doubleValue() {
        return this.numerator / (double)this.denominator;
    }
    
    public Fraction reduce() {
        final int gcd = greatestCommonDivisor(Math.abs(this.numerator), this.denominator);
        return getFraction(this.numerator / gcd, this.denominator / gcd);
    }
    
    public Fraction invert() {
        if (this.numerator == 0) {
            throw new ArithmeticException("Unable to invert a fraction with a zero numerator");
        }
        return getFraction(this.denominator, this.numerator);
    }
    
    public Fraction negate() {
        return getFraction(-this.numerator, this.denominator);
    }
    
    public Fraction abs() {
        if (this.numerator >= 0) {
            return this;
        }
        return getFraction(-this.numerator, this.denominator);
    }
    
    public Fraction pow(final int power) {
        if (power == 1) {
            return this;
        }
        if (power == 0) {
            return Fraction.ONE;
        }
        final double denominatorValue = Math.pow(this.denominator, power);
        final double numeratorValue = Math.pow(this.numerator, power);
        if (numeratorValue > 2.147483647E9 || denominatorValue > 2.147483647E9) {
            throw new ArithmeticException("Integer overflow");
        }
        if (power < 0) {
            return getFraction((int)Math.pow(this.denominator, -power), (int)Math.pow(this.numerator, -power));
        }
        return getFraction((int)Math.pow(this.numerator, power), (int)Math.pow(this.denominator, power));
    }
    
    private static int greatestCommonDivisor(int number1, int number2) {
        for (int remainder = number1 % number2; remainder != 0; remainder = number1 % number2) {
            number1 = number2;
            number2 = remainder;
        }
        return number2;
    }
    
    public Fraction add(final Fraction fraction) {
        if (fraction == null) {
            throw new IllegalArgumentException("The fraction must not be null");
        }
        if (this.numerator == 0) {
            return fraction;
        }
        if (fraction.numerator == 0) {
            return this;
        }
        final int gcd = greatestCommonDivisor(Math.abs(fraction.denominator), Math.abs(this.denominator));
        final int thisResidue = this.denominator / gcd;
        final int thatResidue = fraction.denominator / gcd;
        final double denominatorValue = Math.abs(gcd * (double)thisResidue * thatResidue);
        final double numeratorValue = this.numerator * (double)thatResidue + fraction.numerator * thisResidue;
        if (Math.abs(numeratorValue) > 2.147483647E9 || Math.abs(denominatorValue) > 2.147483647E9) {
            throw new ArithmeticException("Integer overflow");
        }
        return getReducedFraction((int)numeratorValue, (int)denominatorValue);
    }
    
    public Fraction subtract(final Fraction fraction) {
        if (fraction == null) {
            throw new IllegalArgumentException("The fraction must not be null");
        }
        return this.add(fraction.negate());
    }
    
    public Fraction multiplyBy(final Fraction fraction) {
        if (fraction == null) {
            throw new IllegalArgumentException("The fraction must not be null");
        }
        if (this.numerator == 0 || fraction.numerator == 0) {
            return Fraction.ZERO;
        }
        final double numeratorValue = this.numerator * (double)fraction.numerator;
        final double denominatorValue = this.denominator * (double)fraction.denominator;
        if (Math.abs(numeratorValue) > 2.147483647E9 || Math.abs(denominatorValue) > 2.147483647E9) {
            throw new ArithmeticException("Integer overflow");
        }
        return getReducedFraction((int)numeratorValue, (int)denominatorValue);
    }
    
    public Fraction divideBy(final Fraction fraction) {
        if (fraction == null) {
            throw new IllegalArgumentException("The fraction must not be null");
        }
        if (fraction.numerator == 0) {
            throw new ArithmeticException("The fraction to divide by must not be zero");
        }
        if (this.numerator == 0) {
            return Fraction.ZERO;
        }
        final double numeratorValue = this.numerator * (double)fraction.denominator;
        final double denominatorValue = this.denominator * (double)fraction.numerator;
        if (Math.abs(numeratorValue) > 2.147483647E9 || Math.abs(denominatorValue) > 2.147483647E9) {
            throw new ArithmeticException("Integer overflow");
        }
        return getReducedFraction((int)numeratorValue, (int)denominatorValue);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Fraction)) {
            return false;
        }
        final Fraction other = (Fraction)obj;
        return this.numerator == other.numerator && this.denominator == other.denominator;
    }
    
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = 17;
            this.hashCode = 37 * this.hashCode + this.numerator;
            this.hashCode = 37 * this.hashCode + this.denominator;
        }
        return this.hashCode;
    }
    
    public int compareTo(final Object object) {
        final Fraction other = (Fraction)object;
        if (this.numerator == other.numerator && this.denominator == other.denominator) {
            return 0;
        }
        final long first = this.numerator * (long)other.denominator;
        final long second = other.numerator * (long)this.denominator;
        if (first == second) {
            return 0;
        }
        if (first < second) {
            return -1;
        }
        return 1;
    }
    
    public String toString() {
        if (this.toString == null) {
            this.toString = new StringBuffer(32).append(this.numerator).append('/').append(this.denominator).toString();
        }
        return this.toString;
    }
    
    public String toProperString() {
        if (this.toProperString == null) {
            if (this.numerator == 0) {
                this.toProperString = "0";
            }
            else if (this.numerator == this.denominator) {
                this.toProperString = "1";
            }
            else if (Math.abs(this.numerator) > this.denominator) {
                final int properNumerator = this.getProperNumerator();
                if (properNumerator == 0) {
                    this.toProperString = Integer.toString(this.getProperWhole());
                }
                else {
                    this.toProperString = new StringBuffer(32).append(this.getProperWhole()).append(' ').append(properNumerator).append('/').append(this.denominator).toString();
                }
            }
            else {
                this.toProperString = new StringBuffer(32).append(this.numerator).append('/').append(this.denominator).toString();
            }
        }
        return this.toProperString;
    }
    
    static {
        ZERO = new Fraction(0, 1);
        ONE = new Fraction(1, 1);
        ONE_HALF = new Fraction(1, 2);
        ONE_THIRD = new Fraction(1, 3);
        TWO_THIRDS = new Fraction(2, 3);
        ONE_QUARTER = new Fraction(1, 4);
        TWO_QUARTERS = new Fraction(2, 4);
        THREE_QUARTERS = new Fraction(3, 4);
        ONE_FIFTH = new Fraction(1, 5);
        TWO_FIFTHS = new Fraction(2, 5);
        THREE_FIFTHS = new Fraction(3, 5);
        FOUR_FIFTHS = new Fraction(4, 5);
    }
}
