package com.maverick.crypto.publickey;

import java.util.Random;
import com.maverick.crypto.security.SecureRandom;
import java.math.BigInteger;

public final class Dsa
{
    public static byte[] sign(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final byte[] array) {
        final BigInteger bigInteger5 = new BigInteger(1, array);
        final BigInteger bigInteger6 = new BigInteger(1024, SecureRandom.getInstance());
        final BigInteger mod = bigInteger5.mod(bigInteger3);
        final BigInteger mod2 = bigInteger4.modPow(bigInteger6, bigInteger2).mod(bigInteger3);
        final BigInteger mod3 = bigInteger6.modInverse(bigInteger3).multiply(mod.add(bigInteger.multiply(mod2))).mod(bigInteger3);
        final int length = array.length;
        final byte[] array2 = new byte[length * 2];
        System.arraycopy(b(mod2, length), 0, array2, 0, length);
        System.arraycopy(b(mod3, length), 0, array2, length, length);
        return array2;
    }
    
    public static boolean verify(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final byte[] array, final byte[] array2) {
        final int n = array.length / 2;
        final byte[] array3 = new byte[n];
        final byte[] array4 = new byte[n];
        System.arraycopy(array, 0, array3, 0, n);
        System.arraycopy(array, n, array4, 0, n);
        final BigInteger bigInteger5 = new BigInteger(1, array2);
        final BigInteger bigInteger6 = new BigInteger(1, array3);
        if (System.getProperty("maverick.debug.dsa.rvalue", "false").equals("true")) {
            System.out.println("DSA Signature R Value is: " + bigInteger6.toString(16));
        }
        final BigInteger bigInteger7 = new BigInteger(1, array4);
        final BigInteger mod = bigInteger5.mod(bigInteger3);
        final BigInteger modInverse = bigInteger7.modInverse(bigInteger3);
        return bigInteger4.modPow(mod.multiply(modInverse).mod(bigInteger3), bigInteger2).multiply(bigInteger.modPow(bigInteger6.multiply(modInverse).mod(bigInteger3), bigInteger2)).mod(bigInteger2).mod(bigInteger3).compareTo(bigInteger6) == 0;
    }
    
    private static byte[] b(final BigInteger bigInteger, final int n) {
        final byte[] byteArray = bigInteger.toByteArray();
        byte[] array;
        if (byteArray.length > n) {
            array = new byte[n];
            System.arraycopy(byteArray, byteArray.length - n, array, 0, n);
        }
        else if (byteArray.length < n) {
            array = new byte[n];
            System.arraycopy(byteArray, 0, array, n - byteArray.length, byteArray.length);
        }
        else {
            array = byteArray;
        }
        return array;
    }
    
    public static BigInteger generatePublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        return bigInteger.modPow(bigInteger3, bigInteger2);
    }
    
    public static DsaPrivateKey generateKey(final int n, final SecureRandom secureRandom) {
        final BigInteger value = BigInteger.valueOf(0L);
        final d d = new d();
        d.b(n, 80, secureRandom);
        final b b = d.b();
        final BigInteger b2 = b.b();
        final BigInteger c = b.c();
        final BigInteger d2 = b.d();
        BigInteger bigInteger;
        do {
            bigInteger = new BigInteger(160, secureRandom);
        } while (bigInteger.equals(value) || bigInteger.compareTo(b2) >= 0);
        return new DsaPrivateKey(c, b2, d2, bigInteger);
    }
}
