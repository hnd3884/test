package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.math.MathContext;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.io.Serializable;
import org.apache.commons.math3.FieldElement;

public class BigReal implements FieldElement<BigReal>, Comparable<BigReal>, Serializable
{
    public static final BigReal ZERO;
    public static final BigReal ONE;
    private static final long serialVersionUID = 4984534880991310382L;
    private final BigDecimal d;
    private RoundingMode roundingMode;
    private int scale;
    
    public BigReal(final BigDecimal val) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = val;
    }
    
    public BigReal(final BigInteger val) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(val);
    }
    
    public BigReal(final BigInteger unscaledVal, final int scale) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(unscaledVal, scale);
    }
    
    public BigReal(final BigInteger unscaledVal, final int scale, final MathContext mc) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(unscaledVal, scale, mc);
    }
    
    public BigReal(final BigInteger val, final MathContext mc) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(val, mc);
    }
    
    public BigReal(final char[] in) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(in);
    }
    
    public BigReal(final char[] in, final int offset, final int len) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(in, offset, len);
    }
    
    public BigReal(final char[] in, final int offset, final int len, final MathContext mc) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(in, offset, len, mc);
    }
    
    public BigReal(final char[] in, final MathContext mc) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(in, mc);
    }
    
    public BigReal(final double val) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(val);
    }
    
    public BigReal(final double val, final MathContext mc) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(val, mc);
    }
    
    public BigReal(final int val) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(val);
    }
    
    public BigReal(final int val, final MathContext mc) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(val, mc);
    }
    
    public BigReal(final long val) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(val);
    }
    
    public BigReal(final long val, final MathContext mc) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(val, mc);
    }
    
    public BigReal(final String val) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(val);
    }
    
    public BigReal(final String val, final MathContext mc) {
        this.roundingMode = RoundingMode.HALF_UP;
        this.scale = 64;
        this.d = new BigDecimal(val, mc);
    }
    
    public RoundingMode getRoundingMode() {
        return this.roundingMode;
    }
    
    public void setRoundingMode(final RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }
    
    public int getScale() {
        return this.scale;
    }
    
    public void setScale(final int scale) {
        this.scale = scale;
    }
    
    public BigReal add(final BigReal a) {
        return new BigReal(this.d.add(a.d));
    }
    
    public BigReal subtract(final BigReal a) {
        return new BigReal(this.d.subtract(a.d));
    }
    
    public BigReal negate() {
        return new BigReal(this.d.negate());
    }
    
    public BigReal divide(final BigReal a) throws MathArithmeticException {
        try {
            return new BigReal(this.d.divide(a.d, this.scale, this.roundingMode));
        }
        catch (final ArithmeticException e) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NOT_ALLOWED, new Object[0]);
        }
    }
    
    public BigReal reciprocal() throws MathArithmeticException {
        try {
            return new BigReal(BigDecimal.ONE.divide(this.d, this.scale, this.roundingMode));
        }
        catch (final ArithmeticException e) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NOT_ALLOWED, new Object[0]);
        }
    }
    
    public BigReal multiply(final BigReal a) {
        return new BigReal(this.d.multiply(a.d));
    }
    
    public BigReal multiply(final int n) {
        return new BigReal(this.d.multiply(new BigDecimal(n)));
    }
    
    public int compareTo(final BigReal a) {
        return this.d.compareTo(a.d);
    }
    
    public double doubleValue() {
        return this.d.doubleValue();
    }
    
    public BigDecimal bigDecimalValue() {
        return this.d;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof BigReal && this.d.equals(((BigReal)other).d));
    }
    
    @Override
    public int hashCode() {
        return this.d.hashCode();
    }
    
    public Field<BigReal> getField() {
        return BigRealField.getInstance();
    }
    
    static {
        ZERO = new BigReal(BigDecimal.ZERO);
        ONE = new BigReal(BigDecimal.ONE);
    }
}
