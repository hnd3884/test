package org.bouncycastle.util;

import java.util.Random;
import java.security.SecureRandom;
import java.math.BigInteger;

public final class BigIntegers
{
    private static final int MAX_ITERATIONS = 1000;
    private static final BigInteger ZERO;
    
    public static byte[] asUnsignedByteArray(final BigInteger bigInteger) {
        final byte[] byteArray = bigInteger.toByteArray();
        if (byteArray[0] == 0) {
            final byte[] array = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, array, 0, array.length);
            return array;
        }
        return byteArray;
    }
    
    public static byte[] asUnsignedByteArray(final int n, final BigInteger bigInteger) {
        final byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length == n) {
            return byteArray;
        }
        final int n2 = (byteArray[0] == 0) ? 1 : 0;
        final int n3 = byteArray.length - n2;
        if (n3 > n) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }
        final byte[] array = new byte[n];
        System.arraycopy(byteArray, n2, array, array.length - n3, n3);
        return array;
    }
    
    public static BigInteger createRandomInRange(final BigInteger bigInteger, final BigInteger bigInteger2, final SecureRandom secureRandom) {
        final int compareTo = bigInteger.compareTo(bigInteger2);
        if (compareTo >= 0) {
            if (compareTo > 0) {
                throw new IllegalArgumentException("'min' may not be greater than 'max'");
            }
            return bigInteger;
        }
        else {
            if (bigInteger.bitLength() > bigInteger2.bitLength() / 2) {
                return createRandomInRange(BigIntegers.ZERO, bigInteger2.subtract(bigInteger), secureRandom).add(bigInteger);
            }
            for (int i = 0; i < 1000; ++i) {
                final BigInteger bigInteger3 = new BigInteger(bigInteger2.bitLength(), secureRandom);
                if (bigInteger3.compareTo(bigInteger) >= 0 && bigInteger3.compareTo(bigInteger2) <= 0) {
                    return bigInteger3;
                }
            }
            return new BigInteger(bigInteger2.subtract(bigInteger).bitLength() - 1, secureRandom).add(bigInteger);
        }
    }
    
    public static BigInteger fromUnsignedByteArray(final byte[] array) {
        return new BigInteger(1, array);
    }
    
    public static BigInteger fromUnsignedByteArray(final byte[] array, final int n, final int n2) {
        byte[] array2 = array;
        if (n != 0 || n2 != array.length) {
            array2 = new byte[n2];
            System.arraycopy(array, n, array2, 0, n2);
        }
        return new BigInteger(1, array2);
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
    }
}
