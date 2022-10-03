package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.pqc.math.linearalgebra.BigIntUtils;
import org.bouncycastle.pqc.math.linearalgebra.IntegerFunctions;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;
import java.math.BigInteger;

final class Conversions
{
    private static final BigInteger ZERO;
    private static final BigInteger ONE;
    
    private Conversions() {
    }
    
    public static GF2Vector encode(final int n, final int n2, final byte[] array) {
        if (n < n2) {
            throw new IllegalArgumentException("n < t");
        }
        BigInteger bigInteger = IntegerFunctions.binomial(n, n2);
        BigInteger subtract = new BigInteger(1, array);
        if (subtract.compareTo(bigInteger) >= 0) {
            throw new IllegalArgumentException("Encoded number too large.");
        }
        final GF2Vector gf2Vector = new GF2Vector(n);
        int n3 = n;
        int n4 = n2;
        for (int i = 0; i < n; ++i) {
            bigInteger = bigInteger.multiply(BigInteger.valueOf(n3 - n4)).divide(BigInteger.valueOf(n3));
            --n3;
            if (bigInteger.compareTo(subtract) <= 0) {
                gf2Vector.setBit(i);
                subtract = subtract.subtract(bigInteger);
                --n4;
                if (n3 == n4) {
                    bigInteger = Conversions.ONE;
                }
                else {
                    bigInteger = bigInteger.multiply(BigInteger.valueOf(n4 + 1)).divide(BigInteger.valueOf(n3 - n4));
                }
            }
        }
        return gf2Vector;
    }
    
    public static byte[] decode(final int n, final int n2, final GF2Vector gf2Vector) {
        if (gf2Vector.getLength() != n || gf2Vector.getHammingWeight() != n2) {
            throw new IllegalArgumentException("vector has wrong length or hamming weight");
        }
        final int[] vecArray = gf2Vector.getVecArray();
        BigInteger bigInteger = IntegerFunctions.binomial(n, n2);
        BigInteger bigInteger2 = Conversions.ZERO;
        int n3 = n;
        int n4 = n2;
        for (int i = 0; i < n; ++i) {
            bigInteger = bigInteger.multiply(BigInteger.valueOf(n3 - n4)).divide(BigInteger.valueOf(n3));
            --n3;
            if ((vecArray[i >> 5] & 1 << (i & 0x1F)) != 0x0) {
                bigInteger2 = bigInteger2.add(bigInteger);
                --n4;
                if (n3 == n4) {
                    bigInteger = Conversions.ONE;
                }
                else {
                    bigInteger = bigInteger.multiply(BigInteger.valueOf(n4 + 1)).divide(BigInteger.valueOf(n3 - n4));
                }
            }
        }
        return BigIntUtils.toMinimalByteArray(bigInteger2);
    }
    
    public static byte[] signConversion(final int n, final int n2, final byte[] array) {
        if (n < n2) {
            throw new IllegalArgumentException("n < t");
        }
        BigInteger bigInteger = IntegerFunctions.binomial(n, n2);
        final int n3 = bigInteger.bitLength() - 1;
        int n4 = n3 >> 3;
        int n5 = n3 & 0x7;
        if (n5 == 0) {
            --n4;
            n5 = 8;
        }
        int n6 = n >> 3;
        int n7 = n & 0x7;
        if (n7 == 0) {
            --n6;
            n7 = 8;
        }
        final byte[] array2 = new byte[n6 + 1];
        if (array.length < array2.length) {
            System.arraycopy(array, 0, array2, 0, array.length);
            for (int i = array.length; i < array2.length; ++i) {
                array2[i] = 0;
            }
        }
        else {
            System.arraycopy(array, 0, array2, 0, n6);
            array2[n6] = (byte)((1 << n7) - 1 & array[n6]);
        }
        BigInteger bigInteger2 = Conversions.ZERO;
        int n8 = n;
        int n9 = n2;
        for (int j = 0; j < n; ++j) {
            bigInteger = bigInteger.multiply(new BigInteger(Integer.toString(n8 - n9))).divide(new BigInteger(Integer.toString(n8)));
            --n8;
            if ((byte)(1 << (j & 0x7) & array2[j >>> 3]) != 0) {
                bigInteger2 = bigInteger2.add(bigInteger);
                --n9;
                if (n8 == n9) {
                    bigInteger = Conversions.ONE;
                }
                else {
                    bigInteger = bigInteger.multiply(new BigInteger(Integer.toString(n9 + 1))).divide(new BigInteger(Integer.toString(n8 - n9)));
                }
            }
        }
        final byte[] array3 = new byte[n4 + 1];
        final byte[] byteArray = bigInteger2.toByteArray();
        if (byteArray.length < array3.length) {
            System.arraycopy(byteArray, 0, array3, 0, byteArray.length);
            for (int k = byteArray.length; k < array3.length; ++k) {
                array3[k] = 0;
            }
        }
        else {
            System.arraycopy(byteArray, 0, array3, 0, n4);
            array3[n4] = (byte)((1 << n5) - 1 & byteArray[n4]);
        }
        return array3;
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
        ONE = BigInteger.valueOf(1L);
    }
}
