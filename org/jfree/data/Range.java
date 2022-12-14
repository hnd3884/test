package org.jfree.data;

import java.io.Serializable;

public strictfp class Range implements Serializable
{
    private static final long serialVersionUID = -906333695431863380L;
    private double lower;
    private double upper;
    
    public Range(final double lower, final double upper) {
        if (lower > upper) {
            final String msg = "Range(double, double): require lower (" + lower + ") <= upper (" + upper + ").";
            throw new IllegalArgumentException(msg);
        }
        this.lower = lower;
        this.upper = upper;
    }
    
    public strictfp double getLowerBound() {
        return this.lower;
    }
    
    public strictfp double getUpperBound() {
        return this.upper;
    }
    
    public strictfp double getLength() {
        return this.upper - this.lower;
    }
    
    public strictfp double getCentralValue() {
        return this.lower / 2.0 + this.upper / 2.0;
    }
    
    public strictfp boolean contains(final double value) {
        return value >= this.lower && value <= this.upper;
    }
    
    public strictfp boolean intersects(final double b0, final double b1) {
        if (b0 <= this.lower) {
            return b1 > this.lower;
        }
        return b0 < this.upper && b1 >= b0;
    }
    
    public strictfp double constrain(final double value) {
        double result = value;
        if (!this.contains(value)) {
            if (value > this.upper) {
                result = this.upper;
            }
            else if (value < this.lower) {
                result = this.lower;
            }
        }
        return result;
    }
    
    public static strictfp Range combine(final Range range1, final Range range2) {
        if (range1 == null) {
            return range2;
        }
        if (range2 == null) {
            return range1;
        }
        final double l = Math.min(range1.getLowerBound(), range2.getLowerBound());
        final double u = Math.max(range1.getUpperBound(), range2.getUpperBound());
        return new Range(l, u);
    }
    
    public static strictfp Range expandToInclude(final Range range, final double value) {
        if (range == null) {
            return new Range(value, value);
        }
        if (value < range.getLowerBound()) {
            return new Range(value, range.getUpperBound());
        }
        if (value > range.getUpperBound()) {
            return new Range(range.getLowerBound(), value);
        }
        return range;
    }
    
    public static strictfp Range expand(final Range range, final double lowerMargin, final double upperMargin) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        final double length = range.getLength();
        final double lower = length * lowerMargin;
        final double upper = length * upperMargin;
        return new Range(range.getLowerBound() - lower, range.getUpperBound() + upper);
    }
    
    public static strictfp Range shift(final Range base, final double delta) {
        return shift(base, delta, false);
    }
    
    public static strictfp Range shift(final Range base, final double delta, final boolean allowZeroCrossing) {
        if (allowZeroCrossing) {
            return new Range(base.getLowerBound() + delta, base.getUpperBound() + delta);
        }
        return new Range(shiftWithNoZeroCrossing(base.getLowerBound(), delta), shiftWithNoZeroCrossing(base.getUpperBound(), delta));
    }
    
    private static strictfp double shiftWithNoZeroCrossing(final double value, final double delta) {
        if (value > 0.0) {
            return Math.max(value + delta, 0.0);
        }
        if (value < 0.0) {
            return Math.min(value + delta, 0.0);
        }
        return value + delta;
    }
    
    public strictfp boolean equals(final Object obj) {
        if (!(obj instanceof Range)) {
            return false;
        }
        final Range range = (Range)obj;
        return this.lower == range.lower && this.upper == range.upper;
    }
    
    public strictfp int hashCode() {
        long temp = Double.doubleToLongBits(this.lower);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.upper);
        result = 29 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
    
    public strictfp String toString() {
        return "Range[" + this.lower + "," + this.upper + "]";
    }
}
