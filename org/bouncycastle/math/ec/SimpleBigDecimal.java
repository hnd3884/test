package org.bouncycastle.math.ec;

import java.math.BigInteger;

class SimpleBigDecimal
{
    private static final long serialVersionUID = 1L;
    private final BigInteger bigInt;
    private final int scale;
    
    public static SimpleBigDecimal getInstance(final BigInteger bigInteger, final int n) {
        return new SimpleBigDecimal(bigInteger.shiftLeft(n), n);
    }
    
    public SimpleBigDecimal(final BigInteger bigInt, final int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("scale may not be negative");
        }
        this.bigInt = bigInt;
        this.scale = scale;
    }
    
    private void checkScale(final SimpleBigDecimal simpleBigDecimal) {
        if (this.scale != simpleBigDecimal.scale) {
            throw new IllegalArgumentException("Only SimpleBigDecimal of same scale allowed in arithmetic operations");
        }
    }
    
    public SimpleBigDecimal adjustScale(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("scale may not be negative");
        }
        if (n == this.scale) {
            return this;
        }
        return new SimpleBigDecimal(this.bigInt.shiftLeft(n - this.scale), n);
    }
    
    public SimpleBigDecimal add(final SimpleBigDecimal simpleBigDecimal) {
        this.checkScale(simpleBigDecimal);
        return new SimpleBigDecimal(this.bigInt.add(simpleBigDecimal.bigInt), this.scale);
    }
    
    public SimpleBigDecimal add(final BigInteger bigInteger) {
        return new SimpleBigDecimal(this.bigInt.add(bigInteger.shiftLeft(this.scale)), this.scale);
    }
    
    public SimpleBigDecimal negate() {
        return new SimpleBigDecimal(this.bigInt.negate(), this.scale);
    }
    
    public SimpleBigDecimal subtract(final SimpleBigDecimal simpleBigDecimal) {
        return this.add(simpleBigDecimal.negate());
    }
    
    public SimpleBigDecimal subtract(final BigInteger bigInteger) {
        return new SimpleBigDecimal(this.bigInt.subtract(bigInteger.shiftLeft(this.scale)), this.scale);
    }
    
    public SimpleBigDecimal multiply(final SimpleBigDecimal simpleBigDecimal) {
        this.checkScale(simpleBigDecimal);
        return new SimpleBigDecimal(this.bigInt.multiply(simpleBigDecimal.bigInt), this.scale + this.scale);
    }
    
    public SimpleBigDecimal multiply(final BigInteger bigInteger) {
        return new SimpleBigDecimal(this.bigInt.multiply(bigInteger), this.scale);
    }
    
    public SimpleBigDecimal divide(final SimpleBigDecimal simpleBigDecimal) {
        this.checkScale(simpleBigDecimal);
        return new SimpleBigDecimal(this.bigInt.shiftLeft(this.scale).divide(simpleBigDecimal.bigInt), this.scale);
    }
    
    public SimpleBigDecimal divide(final BigInteger bigInteger) {
        return new SimpleBigDecimal(this.bigInt.divide(bigInteger), this.scale);
    }
    
    public SimpleBigDecimal shiftLeft(final int n) {
        return new SimpleBigDecimal(this.bigInt.shiftLeft(n), this.scale);
    }
    
    public int compareTo(final SimpleBigDecimal simpleBigDecimal) {
        this.checkScale(simpleBigDecimal);
        return this.bigInt.compareTo(simpleBigDecimal.bigInt);
    }
    
    public int compareTo(final BigInteger bigInteger) {
        return this.bigInt.compareTo(bigInteger.shiftLeft(this.scale));
    }
    
    public BigInteger floor() {
        return this.bigInt.shiftRight(this.scale);
    }
    
    public BigInteger round() {
        return this.add(new SimpleBigDecimal(ECConstants.ONE, 1).adjustScale(this.scale)).floor();
    }
    
    public int intValue() {
        return this.floor().intValue();
    }
    
    public long longValue() {
        return this.floor().longValue();
    }
    
    public int getScale() {
        return this.scale;
    }
    
    @Override
    public String toString() {
        if (this.scale == 0) {
            return this.bigInt.toString();
        }
        BigInteger bigInteger = this.floor();
        BigInteger bigInteger2 = this.bigInt.subtract(bigInteger.shiftLeft(this.scale));
        if (this.bigInt.signum() == -1) {
            bigInteger2 = ECConstants.ONE.shiftLeft(this.scale).subtract(bigInteger2);
        }
        if (bigInteger.signum() == -1 && !bigInteger2.equals(ECConstants.ZERO)) {
            bigInteger = bigInteger.add(ECConstants.ONE);
        }
        final String string = bigInteger.toString();
        final char[] array = new char[this.scale];
        final String string2 = bigInteger2.toString(2);
        final int length = string2.length();
        final int n = this.scale - length;
        for (int i = 0; i < n; ++i) {
            array[i] = '0';
        }
        for (int j = 0; j < length; ++j) {
            array[n + j] = string2.charAt(j);
        }
        final String s = new String(array);
        final StringBuffer sb = new StringBuffer(string);
        sb.append(".");
        sb.append(s);
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleBigDecimal)) {
            return false;
        }
        final SimpleBigDecimal simpleBigDecimal = (SimpleBigDecimal)o;
        return this.bigInt.equals(simpleBigDecimal.bigInt) && this.scale == simpleBigDecimal.scale;
    }
    
    @Override
    public int hashCode() {
        return this.bigInt.hashCode() ^ this.scale;
    }
}
