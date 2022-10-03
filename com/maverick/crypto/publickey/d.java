package com.maverick.crypto.publickey;

import java.util.Random;
import com.maverick.crypto.digests.SHA1Digest;
import java.math.BigInteger;
import com.maverick.crypto.security.SecureRandom;

class d
{
    private int d;
    private int f;
    private SecureRandom e;
    private static BigInteger c;
    private static BigInteger b;
    
    public void b(final int d, final int f, final SecureRandom e) {
        this.d = d;
        this.f = f;
        this.e = e;
    }
    
    private void b(final byte[] array, final byte[] array2, final int n) {
        final int n2 = (array2[array2.length - 1] & 0xFF) + n;
        array[array2.length - 1] = (byte)n2;
        int n3 = n2 >>> 8;
        for (int i = array2.length - 2; i >= 0; --i) {
            final int n4 = n3 + (array2[i] & 0xFF);
            array[i] = (byte)n4;
            n3 = n4 >>> 8;
        }
    }
    
    public b b() {
        final byte[] array = new byte[20];
        final byte[] array2 = new byte[20];
        final byte[] array3 = new byte[20];
        final byte[] array4 = new byte[20];
        final SHA1Digest sha1Digest = new SHA1Digest();
        final int n = (this.d - 1) / 160;
        final byte[] array5 = new byte[this.d / 8];
        BigInteger bigInteger = null;
        BigInteger subtract = null;
        int i = 0;
        for (int j = 0; j == 0; j = 1) {
            do {
                this.e.nextBytes(array);
                sha1Digest.update(array, 0, array.length);
                sha1Digest.doFinal(array2, 0);
                System.arraycopy(array, 0, array3, 0, array.length);
                this.b(array3, array, 1);
                sha1Digest.update(array3, 0, array3.length);
                sha1Digest.doFinal(array3, 0);
                for (int k = 0; k != array4.length; ++k) {
                    array4[k] = (byte)(array2[k] ^ array3[k]);
                }
                final byte[] array6 = array4;
                final int n2 = 0;
                array6[n2] |= 0xFFFFFF80;
                final byte[] array7 = array4;
                final int n3 = 19;
                array7[n3] |= 0x1;
                bigInteger = new BigInteger(1, array4);
            } while (!bigInteger.isProbablePrime(this.f));
            i = 0;
            for (int n4 = 2; i < 4096; ++i, n4 += n + 1) {
                for (int l = 0; l < n; ++l) {
                    this.b(array2, array, n4 + l);
                    sha1Digest.update(array2, 0, array2.length);
                    sha1Digest.doFinal(array2, 0);
                    System.arraycopy(array2, 0, array5, array5.length - (l + 1) * array2.length, array2.length);
                }
                this.b(array2, array, n4 + n);
                sha1Digest.update(array2, 0, array2.length);
                sha1Digest.doFinal(array2, 0);
                System.arraycopy(array2, array2.length - (array5.length - n * array2.length), array5, 0, array5.length - n * array2.length);
                final byte[] array8 = array5;
                final int n5 = 0;
                array8[n5] |= 0xFFFFFF80;
                final BigInteger bigInteger2 = new BigInteger(1, array5);
                subtract = bigInteger2.subtract(bigInteger2.mod(bigInteger.multiply(com.maverick.crypto.publickey.d.b)).subtract(com.maverick.crypto.publickey.d.c));
                if (subtract.testBit(this.d - 1) && subtract.isProbablePrime(this.f)) {
                    break;
                }
            }
        }
        final BigInteger divide = subtract.subtract(com.maverick.crypto.publickey.d.c).divide(bigInteger);
        BigInteger modPow;
        while (true) {
            final BigInteger bigInteger3 = new BigInteger(this.d, this.e);
            if (bigInteger3.compareTo(com.maverick.crypto.publickey.d.c) > 0) {
                if (bigInteger3.compareTo(subtract.subtract(com.maverick.crypto.publickey.d.c)) >= 0) {
                    continue;
                }
                modPow = bigInteger3.modPow(divide, subtract);
                if (modPow.compareTo(com.maverick.crypto.publickey.d.c) <= 0) {
                    continue;
                }
                break;
            }
        }
        return new b(subtract, bigInteger, modPow, new c(array, i));
    }
    
    static {
        d.c = BigInteger.valueOf(1L);
        d.b = BigInteger.valueOf(2L);
    }
}
