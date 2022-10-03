package org.bouncycastle.math.field;

import java.math.BigInteger;

public abstract class FiniteFields
{
    static final FiniteField GF_2;
    static final FiniteField GF_3;
    
    public static PolynomialExtensionField getBinaryExtensionField(final int[] array) {
        if (array[0] != 0) {
            throw new IllegalArgumentException("Irreducible polynomials in GF(2) must have constant term");
        }
        for (int i = 1; i < array.length; ++i) {
            if (array[i] <= array[i - 1]) {
                throw new IllegalArgumentException("Polynomial exponents must be montonically increasing");
            }
        }
        return new GenericPolynomialExtensionField(FiniteFields.GF_2, new GF2Polynomial(array));
    }
    
    public static FiniteField getPrimeField(final BigInteger bigInteger) {
        final int bitLength = bigInteger.bitLength();
        if (bigInteger.signum() <= 0 || bitLength < 2) {
            throw new IllegalArgumentException("'characteristic' must be >= 2");
        }
        if (bitLength < 3) {
            switch (bigInteger.intValue()) {
                case 2: {
                    return FiniteFields.GF_2;
                }
                case 3: {
                    return FiniteFields.GF_3;
                }
            }
        }
        return new PrimeField(bigInteger);
    }
    
    static {
        GF_2 = new PrimeField(BigInteger.valueOf(2L));
        GF_3 = new PrimeField(BigInteger.valueOf(3L));
    }
}
