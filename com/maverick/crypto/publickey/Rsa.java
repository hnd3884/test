package com.maverick.crypto.publickey;

import java.util.Random;
import com.maverick.crypto.security.SecureRandom;
import java.math.BigInteger;

public final class Rsa
{
    private static BigInteger b;
    
    public static BigInteger doPrivateCrt(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5) {
        return doPrivateCrt(bigInteger, bigInteger3, bigInteger4, getPrimeExponent(bigInteger2, bigInteger3), getPrimeExponent(bigInteger2, bigInteger4), bigInteger5);
    }
    
    public static BigInteger doPrivateCrt(final BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5, final BigInteger bigInteger6) {
        if (!bigInteger6.equals(bigInteger3.modInverse(bigInteger2))) {
            final BigInteger bigInteger7 = bigInteger2;
            bigInteger2 = bigInteger3;
            bigInteger3 = bigInteger7;
            final BigInteger bigInteger8 = bigInteger4;
            bigInteger4 = bigInteger5;
            bigInteger5 = bigInteger8;
        }
        final BigInteger modPow = bigInteger.modPow(bigInteger4, bigInteger2);
        final BigInteger modPow2 = bigInteger.modPow(bigInteger5, bigInteger3);
        return modPow2.add(bigInteger6.multiply(modPow.subtract(modPow2)).mod(bigInteger2).multiply(bigInteger3));
    }
    
    public static BigInteger getPrimeExponent(final BigInteger bigInteger, final BigInteger bigInteger2) {
        return bigInteger.mod(bigInteger2.subtract(Rsa.b));
    }
    
    public static BigInteger padPKCS1(final BigInteger bigInteger, final int n, final int n2) throws IllegalStateException {
        final int n3 = (bigInteger.bitLength() + 7) / 8;
        if (n3 > n2 - 11) {
            throw new IllegalStateException("PKCS1 failed to pad input! input=" + String.valueOf(n3) + " padding=" + String.valueOf(n2));
        }
        final byte[] array = new byte[n2 - n3 - 3 + 1];
        array[0] = 0;
        for (int i = 1; i < n2 - n3 - 3 + 1; ++i) {
            if (n == 1) {
                array[i] = -1;
            }
            else {
                final byte[] array2 = { 0 };
                do {
                    SecureRandom.getInstance().nextBytes(array2);
                } while (array2[0] == 0);
                array[i] = array2[0];
            }
        }
        return BigInteger.valueOf(n).shiftLeft((n2 - 2) * 8).or(new BigInteger(1, array).shiftLeft((n3 + 1) * 8)).or(bigInteger);
    }
    
    public static BigInteger removePKCS1(final BigInteger bigInteger, final int n) throws IllegalStateException {
        final byte[] byteArray = bigInteger.toByteArray();
        if (byteArray[0] != n) {
            throw new IllegalStateException("PKCS1 padding type " + n + " is not valid");
        }
        int n2;
        for (n2 = 1; n2 < byteArray.length && byteArray[n2] != 0; ++n2) {
            if (n == 1 && byteArray[n2] != -1) {
                throw new IllegalStateException("Corrupt data found in expected PKSC1 padding");
            }
        }
        if (n2 == byteArray.length) {
            throw new IllegalStateException("Corrupt data found in expected PKSC1 padding");
        }
        final byte[] array = new byte[byteArray.length - n2];
        System.arraycopy(byteArray, n2, array, 0, array.length);
        return new BigInteger(1, array);
    }
    
    public static RsaPrivateCrtKey generateKey(final int n, final SecureRandom secureRandom) {
        return generateKey(n, BigInteger.valueOf(65537L), secureRandom);
    }
    
    public static RsaPrivateCrtKey generateKey(final int n, final BigInteger bigInteger, final SecureRandom secureRandom) {
        BigInteger bigInteger2 = null;
        BigInteger bigInteger3 = null;
        BigInteger modInverse = null;
        BigInteger modInverse2 = null;
        BigInteger multiply = null;
        int i = 0;
        final BigInteger value = BigInteger.valueOf(1L);
        final int n2 = (n + 1) / 2;
        final int n3 = n - n2;
        while (i == 0) {
            bigInteger2 = new BigInteger(n2, 80, secureRandom);
            bigInteger3 = new BigInteger(n3, 80, secureRandom);
            if (bigInteger2.compareTo(bigInteger3) == 0) {
                continue;
            }
            if (bigInteger2.compareTo(bigInteger3) < 0) {
                final BigInteger bigInteger4 = bigInteger3;
                bigInteger3 = bigInteger2;
                bigInteger2 = bigInteger4;
            }
            if (!bigInteger2.isProbablePrime(25)) {
                continue;
            }
            if (!bigInteger3.isProbablePrime(25)) {
                continue;
            }
            if (bigInteger2.gcd(bigInteger3).compareTo(value) != 0) {
                continue;
            }
            multiply = bigInteger2.multiply(bigInteger3);
            if (multiply.bitLength() != n) {
                continue;
            }
            modInverse = bigInteger.modInverse(bigInteger2.subtract(value).multiply(bigInteger3.subtract(value)));
            modInverse2 = bigInteger3.modInverse(bigInteger2);
            i = 1;
        }
        return new RsaPrivateCrtKey(multiply, bigInteger, modInverse, bigInteger2, bigInteger3, getPrimeExponent(modInverse, bigInteger2), getPrimeExponent(modInverse, bigInteger3), modInverse2);
    }
    
    public static BigInteger doPublic(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        return bigInteger.modPow(bigInteger3, bigInteger2);
    }
    
    public static BigInteger doPrivate(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        return doPublic(bigInteger, bigInteger2, bigInteger3);
    }
    
    static {
        Rsa.b = BigInteger.valueOf(1L);
    }
}
