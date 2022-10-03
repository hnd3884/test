package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public class GF2mField
{
    private int degree;
    private int polynomial;
    
    public GF2mField(final int degree) {
        this.degree = 0;
        if (degree >= 32) {
            throw new IllegalArgumentException(" Error: the degree of field is too large ");
        }
        if (degree < 1) {
            throw new IllegalArgumentException(" Error: the degree of field is non-positive ");
        }
        this.degree = degree;
        this.polynomial = PolynomialRingGF2.getIrreduciblePolynomial(degree);
    }
    
    public GF2mField(final int degree, final int polynomial) {
        this.degree = 0;
        if (degree != PolynomialRingGF2.degree(polynomial)) {
            throw new IllegalArgumentException(" Error: the degree is not correct");
        }
        if (!PolynomialRingGF2.isIrreducible(polynomial)) {
            throw new IllegalArgumentException(" Error: given polynomial is reducible");
        }
        this.degree = degree;
        this.polynomial = polynomial;
    }
    
    public GF2mField(final byte[] array) {
        this.degree = 0;
        if (array.length != 4) {
            throw new IllegalArgumentException("byte array is not an encoded finite field");
        }
        this.polynomial = LittleEndianConversions.OS2IP(array);
        if (!PolynomialRingGF2.isIrreducible(this.polynomial)) {
            throw new IllegalArgumentException("byte array is not an encoded finite field");
        }
        this.degree = PolynomialRingGF2.degree(this.polynomial);
    }
    
    public GF2mField(final GF2mField gf2mField) {
        this.degree = 0;
        this.degree = gf2mField.degree;
        this.polynomial = gf2mField.polynomial;
    }
    
    public int getDegree() {
        return this.degree;
    }
    
    public int getPolynomial() {
        return this.polynomial;
    }
    
    public byte[] getEncoded() {
        return LittleEndianConversions.I2OSP(this.polynomial);
    }
    
    public int add(final int n, final int n2) {
        return n ^ n2;
    }
    
    public int mult(final int n, final int n2) {
        return PolynomialRingGF2.modMultiply(n, n2, this.polynomial);
    }
    
    public int exp(int n, int i) {
        if (i == 0) {
            return 1;
        }
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        int mult = 1;
        if (i < 0) {
            n = this.inverse(n);
            i = -i;
        }
        while (i != 0) {
            if ((i & 0x1) == 0x1) {
                mult = this.mult(mult, n);
            }
            n = this.mult(n, n);
            i >>>= 1;
        }
        return mult;
    }
    
    public int inverse(final int n) {
        return this.exp(n, (1 << this.degree) - 2);
    }
    
    public int sqRoot(int mult) {
        for (int i = 1; i < this.degree; ++i) {
            mult = this.mult(mult, mult);
        }
        return mult;
    }
    
    public int getRandomElement(final SecureRandom secureRandom) {
        return RandUtils.nextInt(secureRandom, 1 << this.degree);
    }
    
    public int getRandomNonZeroElement() {
        return this.getRandomNonZeroElement(new SecureRandom());
    }
    
    public int getRandomNonZeroElement(final SecureRandom secureRandom) {
        int n;
        int n2;
        int n3;
        for (n = 1048576, n2 = 0, n3 = RandUtils.nextInt(secureRandom, 1 << this.degree); n3 == 0 && n2 < n; n3 = RandUtils.nextInt(secureRandom, 1 << this.degree), ++n2) {}
        if (n2 == n) {
            n3 = 1;
        }
        return n3;
    }
    
    public boolean isElementOfThisField(final int n) {
        if (this.degree == 31) {
            return n >= 0;
        }
        return n >= 0 && n < 1 << this.degree;
    }
    
    public String elementToStr(int n) {
        String s = "";
        for (int i = 0; i < this.degree; ++i) {
            if (((byte)n & 0x1) == 0x0) {
                s = "0" + s;
            }
            else {
                s = "1" + s;
            }
            n >>>= 1;
        }
        return s;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof GF2mField)) {
            return false;
        }
        final GF2mField gf2mField = (GF2mField)o;
        return this.degree == gf2mField.degree && this.polynomial == gf2mField.polynomial;
    }
    
    @Override
    public int hashCode() {
        return this.polynomial;
    }
    
    @Override
    public String toString() {
        return "Finite Field GF(2^" + this.degree + ") = GF(2)[X]/<" + polyToString(this.polynomial) + "> ";
    }
    
    private static String polyToString(int i) {
        String string = "";
        if (i == 0) {
            string = "0";
        }
        else {
            if ((byte)(i & 0x1) == 1) {
                string = "1";
            }
            i >>>= 1;
            for (int n = 1; i != 0; i >>>= 1, ++n) {
                if ((byte)(i & 0x1) == 1) {
                    string = string + "+x^" + n;
                }
            }
        }
        return string;
    }
}
