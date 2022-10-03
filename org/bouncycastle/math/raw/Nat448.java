package org.bouncycastle.math.raw;

import org.bouncycastle.util.Pack;
import java.math.BigInteger;

public abstract class Nat448
{
    public static void copy64(final long[] array, final long[] array2) {
        array2[0] = array[0];
        array2[1] = array[1];
        array2[2] = array[2];
        array2[3] = array[3];
        array2[4] = array[4];
        array2[5] = array[5];
        array2[6] = array[6];
    }
    
    public static void copy64(final long[] array, final int n, final long[] array2, final int n2) {
        array2[n2 + 0] = array[n + 0];
        array2[n2 + 1] = array[n + 1];
        array2[n2 + 2] = array[n + 2];
        array2[n2 + 3] = array[n + 3];
        array2[n2 + 4] = array[n + 4];
        array2[n2 + 5] = array[n + 5];
        array2[n2 + 6] = array[n + 6];
    }
    
    public static long[] create64() {
        return new long[7];
    }
    
    public static long[] createExt64() {
        return new long[14];
    }
    
    public static boolean eq64(final long[] array, final long[] array2) {
        for (int i = 6; i >= 0; --i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static long[] fromBigInteger64(BigInteger shiftRight) {
        if (shiftRight.signum() < 0 || shiftRight.bitLength() > 448) {
            throw new IllegalArgumentException();
        }
        final long[] create64 = create64();
        int n = 0;
        while (shiftRight.signum() != 0) {
            create64[n++] = shiftRight.longValue();
            shiftRight = shiftRight.shiftRight(64);
        }
        return create64;
    }
    
    public static boolean isOne64(final long[] array) {
        if (array[0] != 1L) {
            return false;
        }
        for (int i = 1; i < 7; ++i) {
            if (array[i] != 0L) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isZero64(final long[] array) {
        for (int i = 0; i < 7; ++i) {
            if (array[i] != 0L) {
                return false;
            }
        }
        return true;
    }
    
    public static BigInteger toBigInteger64(final long[] array) {
        final byte[] array2 = new byte[56];
        for (int i = 0; i < 7; ++i) {
            final long n = array[i];
            if (n != 0L) {
                Pack.longToBigEndian(n, array2, 6 - i << 3);
            }
        }
        return new BigInteger(1, array2);
    }
}
