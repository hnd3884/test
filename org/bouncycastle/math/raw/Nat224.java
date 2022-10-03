package org.bouncycastle.math.raw;

import org.bouncycastle.util.Pack;
import java.math.BigInteger;

public abstract class Nat224
{
    private static final long M = 4294967295L;
    
    public static int add(final int[] array, final int[] array2, final int[] array3) {
        final long n = 0L + (((long)array[0] & 0xFFFFFFFFL) + ((long)array2[0] & 0xFFFFFFFFL));
        array3[0] = (int)n;
        final long n2 = (n >>> 32) + (((long)array[1] & 0xFFFFFFFFL) + ((long)array2[1] & 0xFFFFFFFFL));
        array3[1] = (int)n2;
        final long n3 = (n2 >>> 32) + (((long)array[2] & 0xFFFFFFFFL) + ((long)array2[2] & 0xFFFFFFFFL));
        array3[2] = (int)n3;
        final long n4 = (n3 >>> 32) + (((long)array[3] & 0xFFFFFFFFL) + ((long)array2[3] & 0xFFFFFFFFL));
        array3[3] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[4] & 0xFFFFFFFFL) + ((long)array2[4] & 0xFFFFFFFFL));
        array3[4] = (int)n5;
        final long n6 = (n5 >>> 32) + (((long)array[5] & 0xFFFFFFFFL) + ((long)array2[5] & 0xFFFFFFFFL));
        array3[5] = (int)n6;
        final long n7 = (n6 >>> 32) + (((long)array[6] & 0xFFFFFFFFL) + ((long)array2[6] & 0xFFFFFFFFL));
        array3[6] = (int)n7;
        return (int)(n7 >>> 32);
    }
    
    public static int add(final int[] array, final int n, final int[] array2, final int n2, final int[] array3, final int n3) {
        final long n4 = 0L + (((long)array[n + 0] & 0xFFFFFFFFL) + ((long)array2[n2 + 0] & 0xFFFFFFFFL));
        array3[n3 + 0] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[n + 1] & 0xFFFFFFFFL) + ((long)array2[n2 + 1] & 0xFFFFFFFFL));
        array3[n3 + 1] = (int)n5;
        final long n6 = (n5 >>> 32) + (((long)array[n + 2] & 0xFFFFFFFFL) + ((long)array2[n2 + 2] & 0xFFFFFFFFL));
        array3[n3 + 2] = (int)n6;
        final long n7 = (n6 >>> 32) + (((long)array[n + 3] & 0xFFFFFFFFL) + ((long)array2[n2 + 3] & 0xFFFFFFFFL));
        array3[n3 + 3] = (int)n7;
        final long n8 = (n7 >>> 32) + (((long)array[n + 4] & 0xFFFFFFFFL) + ((long)array2[n2 + 4] & 0xFFFFFFFFL));
        array3[n3 + 4] = (int)n8;
        final long n9 = (n8 >>> 32) + (((long)array[n + 5] & 0xFFFFFFFFL) + ((long)array2[n2 + 5] & 0xFFFFFFFFL));
        array3[n3 + 5] = (int)n9;
        final long n10 = (n9 >>> 32) + (((long)array[n + 6] & 0xFFFFFFFFL) + ((long)array2[n2 + 6] & 0xFFFFFFFFL));
        array3[n3 + 6] = (int)n10;
        return (int)(n10 >>> 32);
    }
    
    public static int addBothTo(final int[] array, final int[] array2, final int[] array3) {
        final long n = 0L + (((long)array[0] & 0xFFFFFFFFL) + ((long)array2[0] & 0xFFFFFFFFL) + ((long)array3[0] & 0xFFFFFFFFL));
        array3[0] = (int)n;
        final long n2 = (n >>> 32) + (((long)array[1] & 0xFFFFFFFFL) + ((long)array2[1] & 0xFFFFFFFFL) + ((long)array3[1] & 0xFFFFFFFFL));
        array3[1] = (int)n2;
        final long n3 = (n2 >>> 32) + (((long)array[2] & 0xFFFFFFFFL) + ((long)array2[2] & 0xFFFFFFFFL) + ((long)array3[2] & 0xFFFFFFFFL));
        array3[2] = (int)n3;
        final long n4 = (n3 >>> 32) + (((long)array[3] & 0xFFFFFFFFL) + ((long)array2[3] & 0xFFFFFFFFL) + ((long)array3[3] & 0xFFFFFFFFL));
        array3[3] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[4] & 0xFFFFFFFFL) + ((long)array2[4] & 0xFFFFFFFFL) + ((long)array3[4] & 0xFFFFFFFFL));
        array3[4] = (int)n5;
        final long n6 = (n5 >>> 32) + (((long)array[5] & 0xFFFFFFFFL) + ((long)array2[5] & 0xFFFFFFFFL) + ((long)array3[5] & 0xFFFFFFFFL));
        array3[5] = (int)n6;
        final long n7 = (n6 >>> 32) + (((long)array[6] & 0xFFFFFFFFL) + ((long)array2[6] & 0xFFFFFFFFL) + ((long)array3[6] & 0xFFFFFFFFL));
        array3[6] = (int)n7;
        return (int)(n7 >>> 32);
    }
    
    public static int addBothTo(final int[] array, final int n, final int[] array2, final int n2, final int[] array3, final int n3) {
        final long n4 = 0L + (((long)array[n + 0] & 0xFFFFFFFFL) + ((long)array2[n2 + 0] & 0xFFFFFFFFL) + ((long)array3[n3 + 0] & 0xFFFFFFFFL));
        array3[n3 + 0] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[n + 1] & 0xFFFFFFFFL) + ((long)array2[n2 + 1] & 0xFFFFFFFFL) + ((long)array3[n3 + 1] & 0xFFFFFFFFL));
        array3[n3 + 1] = (int)n5;
        final long n6 = (n5 >>> 32) + (((long)array[n + 2] & 0xFFFFFFFFL) + ((long)array2[n2 + 2] & 0xFFFFFFFFL) + ((long)array3[n3 + 2] & 0xFFFFFFFFL));
        array3[n3 + 2] = (int)n6;
        final long n7 = (n6 >>> 32) + (((long)array[n + 3] & 0xFFFFFFFFL) + ((long)array2[n2 + 3] & 0xFFFFFFFFL) + ((long)array3[n3 + 3] & 0xFFFFFFFFL));
        array3[n3 + 3] = (int)n7;
        final long n8 = (n7 >>> 32) + (((long)array[n + 4] & 0xFFFFFFFFL) + ((long)array2[n2 + 4] & 0xFFFFFFFFL) + ((long)array3[n3 + 4] & 0xFFFFFFFFL));
        array3[n3 + 4] = (int)n8;
        final long n9 = (n8 >>> 32) + (((long)array[n + 5] & 0xFFFFFFFFL) + ((long)array2[n2 + 5] & 0xFFFFFFFFL) + ((long)array3[n3 + 5] & 0xFFFFFFFFL));
        array3[n3 + 5] = (int)n9;
        final long n10 = (n9 >>> 32) + (((long)array[n + 6] & 0xFFFFFFFFL) + ((long)array2[n2 + 6] & 0xFFFFFFFFL) + ((long)array3[n3 + 6] & 0xFFFFFFFFL));
        array3[n3 + 6] = (int)n10;
        return (int)(n10 >>> 32);
    }
    
    public static int addTo(final int[] array, final int[] array2) {
        final long n = 0L + (((long)array[0] & 0xFFFFFFFFL) + ((long)array2[0] & 0xFFFFFFFFL));
        array2[0] = (int)n;
        final long n2 = (n >>> 32) + (((long)array[1] & 0xFFFFFFFFL) + ((long)array2[1] & 0xFFFFFFFFL));
        array2[1] = (int)n2;
        final long n3 = (n2 >>> 32) + (((long)array[2] & 0xFFFFFFFFL) + ((long)array2[2] & 0xFFFFFFFFL));
        array2[2] = (int)n3;
        final long n4 = (n3 >>> 32) + (((long)array[3] & 0xFFFFFFFFL) + ((long)array2[3] & 0xFFFFFFFFL));
        array2[3] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[4] & 0xFFFFFFFFL) + ((long)array2[4] & 0xFFFFFFFFL));
        array2[4] = (int)n5;
        final long n6 = (n5 >>> 32) + (((long)array[5] & 0xFFFFFFFFL) + ((long)array2[5] & 0xFFFFFFFFL));
        array2[5] = (int)n6;
        final long n7 = (n6 >>> 32) + (((long)array[6] & 0xFFFFFFFFL) + ((long)array2[6] & 0xFFFFFFFFL));
        array2[6] = (int)n7;
        return (int)(n7 >>> 32);
    }
    
    public static int addTo(final int[] array, final int n, final int[] array2, final int n2, final int n3) {
        final long n4 = ((long)n3 & 0xFFFFFFFFL) + (((long)array[n + 0] & 0xFFFFFFFFL) + ((long)array2[n2 + 0] & 0xFFFFFFFFL));
        array2[n2 + 0] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[n + 1] & 0xFFFFFFFFL) + ((long)array2[n2 + 1] & 0xFFFFFFFFL));
        array2[n2 + 1] = (int)n5;
        final long n6 = (n5 >>> 32) + (((long)array[n + 2] & 0xFFFFFFFFL) + ((long)array2[n2 + 2] & 0xFFFFFFFFL));
        array2[n2 + 2] = (int)n6;
        final long n7 = (n6 >>> 32) + (((long)array[n + 3] & 0xFFFFFFFFL) + ((long)array2[n2 + 3] & 0xFFFFFFFFL));
        array2[n2 + 3] = (int)n7;
        final long n8 = (n7 >>> 32) + (((long)array[n + 4] & 0xFFFFFFFFL) + ((long)array2[n2 + 4] & 0xFFFFFFFFL));
        array2[n2 + 4] = (int)n8;
        final long n9 = (n8 >>> 32) + (((long)array[n + 5] & 0xFFFFFFFFL) + ((long)array2[n2 + 5] & 0xFFFFFFFFL));
        array2[n2 + 5] = (int)n9;
        final long n10 = (n9 >>> 32) + (((long)array[n + 6] & 0xFFFFFFFFL) + ((long)array2[n2 + 6] & 0xFFFFFFFFL));
        array2[n2 + 6] = (int)n10;
        return (int)(n10 >>> 32);
    }
    
    public static int addToEachOther(final int[] array, final int n, final int[] array2, final int n2) {
        final long n3 = 0L + (((long)array[n + 0] & 0xFFFFFFFFL) + ((long)array2[n2 + 0] & 0xFFFFFFFFL));
        array[n + 0] = (int)n3;
        array2[n2 + 0] = (int)n3;
        final long n4 = (n3 >>> 32) + (((long)array[n + 1] & 0xFFFFFFFFL) + ((long)array2[n2 + 1] & 0xFFFFFFFFL));
        array[n + 1] = (int)n4;
        array2[n2 + 1] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[n + 2] & 0xFFFFFFFFL) + ((long)array2[n2 + 2] & 0xFFFFFFFFL));
        array[n + 2] = (int)n5;
        array2[n2 + 2] = (int)n5;
        final long n6 = (n5 >>> 32) + (((long)array[n + 3] & 0xFFFFFFFFL) + ((long)array2[n2 + 3] & 0xFFFFFFFFL));
        array[n + 3] = (int)n6;
        array2[n2 + 3] = (int)n6;
        final long n7 = (n6 >>> 32) + (((long)array[n + 4] & 0xFFFFFFFFL) + ((long)array2[n2 + 4] & 0xFFFFFFFFL));
        array[n + 4] = (int)n7;
        array2[n2 + 4] = (int)n7;
        final long n8 = (n7 >>> 32) + (((long)array[n + 5] & 0xFFFFFFFFL) + ((long)array2[n2 + 5] & 0xFFFFFFFFL));
        array[n + 5] = (int)n8;
        array2[n2 + 5] = (int)n8;
        final long n9 = (n8 >>> 32) + (((long)array[n + 6] & 0xFFFFFFFFL) + ((long)array2[n2 + 6] & 0xFFFFFFFFL));
        array[n + 6] = (int)n9;
        array2[n2 + 6] = (int)n9;
        return (int)(n9 >>> 32);
    }
    
    public static void copy(final int[] array, final int[] array2) {
        array2[0] = array[0];
        array2[1] = array[1];
        array2[2] = array[2];
        array2[3] = array[3];
        array2[4] = array[4];
        array2[5] = array[5];
        array2[6] = array[6];
    }
    
    public static void copy(final int[] array, final int n, final int[] array2, final int n2) {
        array2[n2 + 0] = array[n + 0];
        array2[n2 + 1] = array[n + 1];
        array2[n2 + 2] = array[n + 2];
        array2[n2 + 3] = array[n + 3];
        array2[n2 + 4] = array[n + 4];
        array2[n2 + 5] = array[n + 5];
        array2[n2 + 6] = array[n + 6];
    }
    
    public static int[] create() {
        return new int[7];
    }
    
    public static int[] createExt() {
        return new int[14];
    }
    
    public static boolean diff(final int[] array, final int n, final int[] array2, final int n2, final int[] array3, final int n3) {
        final boolean gte = gte(array, n, array2, n2);
        if (gte) {
            sub(array, n, array2, n2, array3, n3);
        }
        else {
            sub(array2, n2, array, n, array3, n3);
        }
        return gte;
    }
    
    public static boolean eq(final int[] array, final int[] array2) {
        for (int i = 6; i >= 0; --i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static int[] fromBigInteger(BigInteger shiftRight) {
        if (shiftRight.signum() < 0 || shiftRight.bitLength() > 224) {
            throw new IllegalArgumentException();
        }
        final int[] create = create();
        int n = 0;
        while (shiftRight.signum() != 0) {
            create[n++] = shiftRight.intValue();
            shiftRight = shiftRight.shiftRight(32);
        }
        return create;
    }
    
    public static int getBit(final int[] array, final int n) {
        if (n == 0) {
            return array[0] & 0x1;
        }
        final int n2 = n >> 5;
        if (n2 < 0 || n2 >= 7) {
            return 0;
        }
        return array[n2] >>> (n & 0x1F) & 0x1;
    }
    
    public static boolean gte(final int[] array, final int[] array2) {
        for (int i = 6; i >= 0; --i) {
            final int n = array[i] ^ Integer.MIN_VALUE;
            final int n2 = array2[i] ^ Integer.MIN_VALUE;
            if (n < n2) {
                return false;
            }
            if (n > n2) {
                return true;
            }
        }
        return true;
    }
    
    public static boolean gte(final int[] array, final int n, final int[] array2, final int n2) {
        for (int i = 6; i >= 0; --i) {
            final int n3 = array[n + i] ^ Integer.MIN_VALUE;
            final int n4 = array2[n2 + i] ^ Integer.MIN_VALUE;
            if (n3 < n4) {
                return false;
            }
            if (n3 > n4) {
                return true;
            }
        }
        return true;
    }
    
    public static boolean isOne(final int[] array) {
        if (array[0] != 1) {
            return false;
        }
        for (int i = 1; i < 7; ++i) {
            if (array[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isZero(final int[] array) {
        for (int i = 0; i < 7; ++i) {
            if (array[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    public static void mul(final int[] array, final int[] array2, final int[] array3) {
        final long n = (long)array2[0] & 0xFFFFFFFFL;
        final long n2 = (long)array2[1] & 0xFFFFFFFFL;
        final long n3 = (long)array2[2] & 0xFFFFFFFFL;
        final long n4 = (long)array2[3] & 0xFFFFFFFFL;
        final long n5 = (long)array2[4] & 0xFFFFFFFFL;
        final long n6 = (long)array2[5] & 0xFFFFFFFFL;
        final long n7 = (long)array2[6] & 0xFFFFFFFFL;
        final long n8 = 0L;
        final long n9 = (long)array[0] & 0xFFFFFFFFL;
        final long n10 = n8 + n9 * n;
        array3[0] = (int)n10;
        final long n11 = (n10 >>> 32) + n9 * n2;
        array3[1] = (int)n11;
        final long n12 = (n11 >>> 32) + n9 * n3;
        array3[2] = (int)n12;
        final long n13 = (n12 >>> 32) + n9 * n4;
        array3[3] = (int)n13;
        final long n14 = (n13 >>> 32) + n9 * n5;
        array3[4] = (int)n14;
        final long n15 = (n14 >>> 32) + n9 * n6;
        array3[5] = (int)n15;
        final long n16 = (n15 >>> 32) + n9 * n7;
        array3[6] = (int)n16;
        array3[7] = (int)(n16 >>> 32);
        for (int i = 1; i < 7; ++i) {
            final long n17 = 0L;
            final long n18 = (long)array[i] & 0xFFFFFFFFL;
            final long n19 = n17 + (n18 * n + ((long)array3[i + 0] & 0xFFFFFFFFL));
            array3[i + 0] = (int)n19;
            final long n20 = (n19 >>> 32) + (n18 * n2 + ((long)array3[i + 1] & 0xFFFFFFFFL));
            array3[i + 1] = (int)n20;
            final long n21 = (n20 >>> 32) + (n18 * n3 + ((long)array3[i + 2] & 0xFFFFFFFFL));
            array3[i + 2] = (int)n21;
            final long n22 = (n21 >>> 32) + (n18 * n4 + ((long)array3[i + 3] & 0xFFFFFFFFL));
            array3[i + 3] = (int)n22;
            final long n23 = (n22 >>> 32) + (n18 * n5 + ((long)array3[i + 4] & 0xFFFFFFFFL));
            array3[i + 4] = (int)n23;
            final long n24 = (n23 >>> 32) + (n18 * n6 + ((long)array3[i + 5] & 0xFFFFFFFFL));
            array3[i + 5] = (int)n24;
            final long n25 = (n24 >>> 32) + (n18 * n7 + ((long)array3[i + 6] & 0xFFFFFFFFL));
            array3[i + 6] = (int)n25;
            array3[i + 7] = (int)(n25 >>> 32);
        }
    }
    
    public static void mul(final int[] array, final int n, final int[] array2, final int n2, final int[] array3, int n3) {
        final long n4 = (long)array2[n2 + 0] & 0xFFFFFFFFL;
        final long n5 = (long)array2[n2 + 1] & 0xFFFFFFFFL;
        final long n6 = (long)array2[n2 + 2] & 0xFFFFFFFFL;
        final long n7 = (long)array2[n2 + 3] & 0xFFFFFFFFL;
        final long n8 = (long)array2[n2 + 4] & 0xFFFFFFFFL;
        final long n9 = (long)array2[n2 + 5] & 0xFFFFFFFFL;
        final long n10 = (long)array2[n2 + 6] & 0xFFFFFFFFL;
        final long n11 = 0L;
        final long n12 = (long)array[n + 0] & 0xFFFFFFFFL;
        final long n13 = n11 + n12 * n4;
        array3[n3 + 0] = (int)n13;
        final long n14 = (n13 >>> 32) + n12 * n5;
        array3[n3 + 1] = (int)n14;
        final long n15 = (n14 >>> 32) + n12 * n6;
        array3[n3 + 2] = (int)n15;
        final long n16 = (n15 >>> 32) + n12 * n7;
        array3[n3 + 3] = (int)n16;
        final long n17 = (n16 >>> 32) + n12 * n8;
        array3[n3 + 4] = (int)n17;
        final long n18 = (n17 >>> 32) + n12 * n9;
        array3[n3 + 5] = (int)n18;
        final long n19 = (n18 >>> 32) + n12 * n10;
        array3[n3 + 6] = (int)n19;
        array3[n3 + 7] = (int)(n19 >>> 32);
        for (int i = 1; i < 7; ++i) {
            ++n3;
            final long n20 = 0L;
            final long n21 = (long)array[n + i] & 0xFFFFFFFFL;
            final long n22 = n20 + (n21 * n4 + ((long)array3[n3 + 0] & 0xFFFFFFFFL));
            array3[n3 + 0] = (int)n22;
            final long n23 = (n22 >>> 32) + (n21 * n5 + ((long)array3[n3 + 1] & 0xFFFFFFFFL));
            array3[n3 + 1] = (int)n23;
            final long n24 = (n23 >>> 32) + (n21 * n6 + ((long)array3[n3 + 2] & 0xFFFFFFFFL));
            array3[n3 + 2] = (int)n24;
            final long n25 = (n24 >>> 32) + (n21 * n7 + ((long)array3[n3 + 3] & 0xFFFFFFFFL));
            array3[n3 + 3] = (int)n25;
            final long n26 = (n25 >>> 32) + (n21 * n8 + ((long)array3[n3 + 4] & 0xFFFFFFFFL));
            array3[n3 + 4] = (int)n26;
            final long n27 = (n26 >>> 32) + (n21 * n9 + ((long)array3[n3 + 5] & 0xFFFFFFFFL));
            array3[n3 + 5] = (int)n27;
            final long n28 = (n27 >>> 32) + (n21 * n10 + ((long)array3[n3 + 6] & 0xFFFFFFFFL));
            array3[n3 + 6] = (int)n28;
            array3[n3 + 7] = (int)(n28 >>> 32);
        }
    }
    
    public static int mulAddTo(final int[] array, final int[] array2, final int[] array3) {
        final long n = (long)array2[0] & 0xFFFFFFFFL;
        final long n2 = (long)array2[1] & 0xFFFFFFFFL;
        final long n3 = (long)array2[2] & 0xFFFFFFFFL;
        final long n4 = (long)array2[3] & 0xFFFFFFFFL;
        final long n5 = (long)array2[4] & 0xFFFFFFFFL;
        final long n6 = (long)array2[5] & 0xFFFFFFFFL;
        final long n7 = (long)array2[6] & 0xFFFFFFFFL;
        long n8 = 0L;
        for (int i = 0; i < 7; ++i) {
            final long n9 = 0L;
            final long n10 = (long)array[i] & 0xFFFFFFFFL;
            final long n11 = n9 + (n10 * n + ((long)array3[i + 0] & 0xFFFFFFFFL));
            array3[i + 0] = (int)n11;
            final long n12 = (n11 >>> 32) + (n10 * n2 + ((long)array3[i + 1] & 0xFFFFFFFFL));
            array3[i + 1] = (int)n12;
            final long n13 = (n12 >>> 32) + (n10 * n3 + ((long)array3[i + 2] & 0xFFFFFFFFL));
            array3[i + 2] = (int)n13;
            final long n14 = (n13 >>> 32) + (n10 * n4 + ((long)array3[i + 3] & 0xFFFFFFFFL));
            array3[i + 3] = (int)n14;
            final long n15 = (n14 >>> 32) + (n10 * n5 + ((long)array3[i + 4] & 0xFFFFFFFFL));
            array3[i + 4] = (int)n15;
            final long n16 = (n15 >>> 32) + (n10 * n6 + ((long)array3[i + 5] & 0xFFFFFFFFL));
            array3[i + 5] = (int)n16;
            final long n17 = (n16 >>> 32) + (n10 * n7 + ((long)array3[i + 6] & 0xFFFFFFFFL));
            array3[i + 6] = (int)n17;
            final long n18 = (n17 >>> 32) + (n8 + ((long)array3[i + 7] & 0xFFFFFFFFL));
            array3[i + 7] = (int)n18;
            n8 = n18 >>> 32;
        }
        return (int)n8;
    }
    
    public static int mulAddTo(final int[] array, final int n, final int[] array2, final int n2, final int[] array3, int n3) {
        final long n4 = (long)array2[n2 + 0] & 0xFFFFFFFFL;
        final long n5 = (long)array2[n2 + 1] & 0xFFFFFFFFL;
        final long n6 = (long)array2[n2 + 2] & 0xFFFFFFFFL;
        final long n7 = (long)array2[n2 + 3] & 0xFFFFFFFFL;
        final long n8 = (long)array2[n2 + 4] & 0xFFFFFFFFL;
        final long n9 = (long)array2[n2 + 5] & 0xFFFFFFFFL;
        final long n10 = (long)array2[n2 + 6] & 0xFFFFFFFFL;
        long n11 = 0L;
        for (int i = 0; i < 7; ++i) {
            final long n12 = 0L;
            final long n13 = (long)array[n + i] & 0xFFFFFFFFL;
            final long n14 = n12 + (n13 * n4 + ((long)array3[n3 + 0] & 0xFFFFFFFFL));
            array3[n3 + 0] = (int)n14;
            final long n15 = (n14 >>> 32) + (n13 * n5 + ((long)array3[n3 + 1] & 0xFFFFFFFFL));
            array3[n3 + 1] = (int)n15;
            final long n16 = (n15 >>> 32) + (n13 * n6 + ((long)array3[n3 + 2] & 0xFFFFFFFFL));
            array3[n3 + 2] = (int)n16;
            final long n17 = (n16 >>> 32) + (n13 * n7 + ((long)array3[n3 + 3] & 0xFFFFFFFFL));
            array3[n3 + 3] = (int)n17;
            final long n18 = (n17 >>> 32) + (n13 * n8 + ((long)array3[n3 + 4] & 0xFFFFFFFFL));
            array3[n3 + 4] = (int)n18;
            final long n19 = (n18 >>> 32) + (n13 * n9 + ((long)array3[n3 + 5] & 0xFFFFFFFFL));
            array3[n3 + 5] = (int)n19;
            final long n20 = (n19 >>> 32) + (n13 * n10 + ((long)array3[n3 + 6] & 0xFFFFFFFFL));
            array3[n3 + 6] = (int)n20;
            final long n21 = (n20 >>> 32) + (n11 + ((long)array3[n3 + 7] & 0xFFFFFFFFL));
            array3[n3 + 7] = (int)n21;
            n11 = n21 >>> 32;
            ++n3;
        }
        return (int)n11;
    }
    
    public static long mul33Add(final int n, final int[] array, final int n2, final int[] array2, final int n3, final int[] array3, final int n4) {
        final long n5 = 0L;
        final long n6 = (long)n & 0xFFFFFFFFL;
        final long n7 = (long)array[n2 + 0] & 0xFFFFFFFFL;
        final long n8 = n5 + (n6 * n7 + ((long)array2[n3 + 0] & 0xFFFFFFFFL));
        array3[n4 + 0] = (int)n8;
        final long n9 = n8 >>> 32;
        final long n10 = (long)array[n2 + 1] & 0xFFFFFFFFL;
        final long n11 = n9 + (n6 * n10 + n7 + ((long)array2[n3 + 1] & 0xFFFFFFFFL));
        array3[n4 + 1] = (int)n11;
        final long n12 = n11 >>> 32;
        final long n13 = (long)array[n2 + 2] & 0xFFFFFFFFL;
        final long n14 = n12 + (n6 * n13 + n10 + ((long)array2[n3 + 2] & 0xFFFFFFFFL));
        array3[n4 + 2] = (int)n14;
        final long n15 = n14 >>> 32;
        final long n16 = (long)array[n2 + 3] & 0xFFFFFFFFL;
        final long n17 = n15 + (n6 * n16 + n13 + ((long)array2[n3 + 3] & 0xFFFFFFFFL));
        array3[n4 + 3] = (int)n17;
        final long n18 = n17 >>> 32;
        final long n19 = (long)array[n2 + 4] & 0xFFFFFFFFL;
        final long n20 = n18 + (n6 * n19 + n16 + ((long)array2[n3 + 4] & 0xFFFFFFFFL));
        array3[n4 + 4] = (int)n20;
        final long n21 = n20 >>> 32;
        final long n22 = (long)array[n2 + 5] & 0xFFFFFFFFL;
        final long n23 = n21 + (n6 * n22 + n19 + ((long)array2[n3 + 5] & 0xFFFFFFFFL));
        array3[n4 + 5] = (int)n23;
        final long n24 = n23 >>> 32;
        final long n25 = (long)array[n2 + 6] & 0xFFFFFFFFL;
        final long n26 = n24 + (n6 * n25 + n22 + ((long)array2[n3 + 6] & 0xFFFFFFFFL));
        array3[n4 + 6] = (int)n26;
        return (n26 >>> 32) + n25;
    }
    
    public static int mulByWord(final int n, final int[] array) {
        final long n2 = 0L;
        final long n3 = (long)n & 0xFFFFFFFFL;
        final long n4 = n2 + n3 * ((long)array[0] & 0xFFFFFFFFL);
        array[0] = (int)n4;
        final long n5 = (n4 >>> 32) + n3 * ((long)array[1] & 0xFFFFFFFFL);
        array[1] = (int)n5;
        final long n6 = (n5 >>> 32) + n3 * ((long)array[2] & 0xFFFFFFFFL);
        array[2] = (int)n6;
        final long n7 = (n6 >>> 32) + n3 * ((long)array[3] & 0xFFFFFFFFL);
        array[3] = (int)n7;
        final long n8 = (n7 >>> 32) + n3 * ((long)array[4] & 0xFFFFFFFFL);
        array[4] = (int)n8;
        final long n9 = (n8 >>> 32) + n3 * ((long)array[5] & 0xFFFFFFFFL);
        array[5] = (int)n9;
        final long n10 = (n9 >>> 32) + n3 * ((long)array[6] & 0xFFFFFFFFL);
        array[6] = (int)n10;
        return (int)(n10 >>> 32);
    }
    
    public static int mulByWordAddTo(final int n, final int[] array, final int[] array2) {
        final long n2 = 0L;
        final long n3 = (long)n & 0xFFFFFFFFL;
        final long n4 = n2 + (n3 * ((long)array2[0] & 0xFFFFFFFFL) + ((long)array[0] & 0xFFFFFFFFL));
        array2[0] = (int)n4;
        final long n5 = (n4 >>> 32) + (n3 * ((long)array2[1] & 0xFFFFFFFFL) + ((long)array[1] & 0xFFFFFFFFL));
        array2[1] = (int)n5;
        final long n6 = (n5 >>> 32) + (n3 * ((long)array2[2] & 0xFFFFFFFFL) + ((long)array[2] & 0xFFFFFFFFL));
        array2[2] = (int)n6;
        final long n7 = (n6 >>> 32) + (n3 * ((long)array2[3] & 0xFFFFFFFFL) + ((long)array[3] & 0xFFFFFFFFL));
        array2[3] = (int)n7;
        final long n8 = (n7 >>> 32) + (n3 * ((long)array2[4] & 0xFFFFFFFFL) + ((long)array[4] & 0xFFFFFFFFL));
        array2[4] = (int)n8;
        final long n9 = (n8 >>> 32) + (n3 * ((long)array2[5] & 0xFFFFFFFFL) + ((long)array[5] & 0xFFFFFFFFL));
        array2[5] = (int)n9;
        final long n10 = (n9 >>> 32) + (n3 * ((long)array2[6] & 0xFFFFFFFFL) + ((long)array[6] & 0xFFFFFFFFL));
        array2[6] = (int)n10;
        return (int)(n10 >>> 32);
    }
    
    public static int mulWordAddTo(final int n, final int[] array, final int n2, final int[] array2, final int n3) {
        final long n4 = 0L;
        final long n5 = (long)n & 0xFFFFFFFFL;
        final long n6 = n4 + (n5 * ((long)array[n2 + 0] & 0xFFFFFFFFL) + ((long)array2[n3 + 0] & 0xFFFFFFFFL));
        array2[n3 + 0] = (int)n6;
        final long n7 = (n6 >>> 32) + (n5 * ((long)array[n2 + 1] & 0xFFFFFFFFL) + ((long)array2[n3 + 1] & 0xFFFFFFFFL));
        array2[n3 + 1] = (int)n7;
        final long n8 = (n7 >>> 32) + (n5 * ((long)array[n2 + 2] & 0xFFFFFFFFL) + ((long)array2[n3 + 2] & 0xFFFFFFFFL));
        array2[n3 + 2] = (int)n8;
        final long n9 = (n8 >>> 32) + (n5 * ((long)array[n2 + 3] & 0xFFFFFFFFL) + ((long)array2[n3 + 3] & 0xFFFFFFFFL));
        array2[n3 + 3] = (int)n9;
        final long n10 = (n9 >>> 32) + (n5 * ((long)array[n2 + 4] & 0xFFFFFFFFL) + ((long)array2[n3 + 4] & 0xFFFFFFFFL));
        array2[n3 + 4] = (int)n10;
        final long n11 = (n10 >>> 32) + (n5 * ((long)array[n2 + 5] & 0xFFFFFFFFL) + ((long)array2[n3 + 5] & 0xFFFFFFFFL));
        array2[n3 + 5] = (int)n11;
        final long n12 = (n11 >>> 32) + (n5 * ((long)array[n2 + 6] & 0xFFFFFFFFL) + ((long)array2[n3 + 6] & 0xFFFFFFFFL));
        array2[n3 + 6] = (int)n12;
        return (int)(n12 >>> 32);
    }
    
    public static int mul33DWordAdd(final int n, final long n2, final int[] array, final int n3) {
        final long n4 = 0L;
        final long n5 = (long)n & 0xFFFFFFFFL;
        final long n6 = n2 & 0xFFFFFFFFL;
        final long n7 = n4 + (n5 * n6 + ((long)array[n3 + 0] & 0xFFFFFFFFL));
        array[n3 + 0] = (int)n7;
        final long n8 = n7 >>> 32;
        final long n9 = n2 >>> 32;
        final long n10 = n8 + (n5 * n9 + n6 + ((long)array[n3 + 1] & 0xFFFFFFFFL));
        array[n3 + 1] = (int)n10;
        final long n11 = (n10 >>> 32) + (n9 + ((long)array[n3 + 2] & 0xFFFFFFFFL));
        array[n3 + 2] = (int)n11;
        final long n12 = (n11 >>> 32) + ((long)array[n3 + 3] & 0xFFFFFFFFL);
        array[n3 + 3] = (int)n12;
        return (n12 >>> 32 == 0L) ? 0 : Nat.incAt(7, array, n3, 4);
    }
    
    public static int mul33WordAdd(final int n, final int n2, final int[] array, final int n3) {
        final long n4 = 0L;
        final long n5 = (long)n & 0xFFFFFFFFL;
        final long n6 = (long)n2 & 0xFFFFFFFFL;
        final long n7 = n4 + (n6 * n5 + ((long)array[n3 + 0] & 0xFFFFFFFFL));
        array[n3 + 0] = (int)n7;
        final long n8 = (n7 >>> 32) + (n6 + ((long)array[n3 + 1] & 0xFFFFFFFFL));
        array[n3 + 1] = (int)n8;
        final long n9 = (n8 >>> 32) + ((long)array[n3 + 2] & 0xFFFFFFFFL);
        array[n3 + 2] = (int)n9;
        return (n9 >>> 32 == 0L) ? 0 : Nat.incAt(7, array, n3, 3);
    }
    
    public static int mulWordDwordAdd(final int n, final long n2, final int[] array, final int n3) {
        final long n4 = 0L;
        final long n5 = (long)n & 0xFFFFFFFFL;
        final long n6 = n4 + (n5 * (n2 & 0xFFFFFFFFL) + ((long)array[n3 + 0] & 0xFFFFFFFFL));
        array[n3 + 0] = (int)n6;
        final long n7 = (n6 >>> 32) + (n5 * (n2 >>> 32) + ((long)array[n3 + 1] & 0xFFFFFFFFL));
        array[n3 + 1] = (int)n7;
        final long n8 = (n7 >>> 32) + ((long)array[n3 + 2] & 0xFFFFFFFFL);
        array[n3 + 2] = (int)n8;
        return (n8 >>> 32 == 0L) ? 0 : Nat.incAt(7, array, n3, 3);
    }
    
    public static int mulWord(final int n, final int[] array, final int[] array2, final int n2) {
        long n3 = 0L;
        final long n4 = (long)n & 0xFFFFFFFFL;
        int n5 = 0;
        do {
            final long n6 = n3 + n4 * ((long)array[n5] & 0xFFFFFFFFL);
            array2[n2 + n5] = (int)n6;
            n3 = n6 >>> 32;
        } while (++n5 < 7);
        return (int)n3;
    }
    
    public static void square(final int[] array, final int[] array2) {
        final long n = (long)array[0] & 0xFFFFFFFFL;
        int n2 = 0;
        int i = 6;
        int n3 = 14;
        do {
            final long n4 = (long)array[i--] & 0xFFFFFFFFL;
            final long n5 = n4 * n4;
            array2[--n3] = (n2 << 31 | (int)(n5 >>> 33));
            array2[--n3] = (int)(n5 >>> 1);
            n2 = (int)n5;
        } while (i > 0);
        final long n6 = n * n;
        final long n7 = ((long)(n2 << 31) & 0xFFFFFFFFL) | n6 >>> 33;
        array2[0] = (int)n6;
        final int n8 = (int)(n6 >>> 32) & 0x1;
        final long n9 = (long)array[1] & 0xFFFFFFFFL;
        final long n10 = (long)array2[2] & 0xFFFFFFFFL;
        final long n11 = n7 + n9 * n;
        final int n12 = (int)n11;
        array2[1] = (n12 << 1 | n8);
        final int n13 = n12 >>> 31;
        final long n14 = n10 + (n11 >>> 32);
        final long n15 = (long)array[2] & 0xFFFFFFFFL;
        final long n16 = (long)array2[3] & 0xFFFFFFFFL;
        final long n17 = (long)array2[4] & 0xFFFFFFFFL;
        final long n18 = n14 + n15 * n;
        final int n19 = (int)n18;
        array2[2] = (n19 << 1 | n13);
        final int n20 = n19 >>> 31;
        final long n21 = n16 + ((n18 >>> 32) + n15 * n9);
        final long n22 = n17 + (n21 >>> 32);
        final long n23 = n21 & 0xFFFFFFFFL;
        final long n24 = (long)array[3] & 0xFFFFFFFFL;
        final long n25 = ((long)array2[5] & 0xFFFFFFFFL) + (n22 >>> 32);
        final long n26 = n22 & 0xFFFFFFFFL;
        final long n27 = ((long)array2[6] & 0xFFFFFFFFL) + (n25 >>> 32);
        final long n28 = n25 & 0xFFFFFFFFL;
        final long n29 = n23 + n24 * n;
        final int n30 = (int)n29;
        array2[3] = (n30 << 1 | n20);
        final int n31 = n30 >>> 31;
        final long n32 = n26 + ((n29 >>> 32) + n24 * n9);
        final long n33 = n28 + ((n32 >>> 32) + n24 * n15);
        final long n34 = n32 & 0xFFFFFFFFL;
        final long n35 = n27 + (n33 >>> 32);
        final long n36 = n33 & 0xFFFFFFFFL;
        final long n37 = (long)array[4] & 0xFFFFFFFFL;
        final long n38 = ((long)array2[7] & 0xFFFFFFFFL) + (n35 >>> 32);
        final long n39 = n35 & 0xFFFFFFFFL;
        final long n40 = ((long)array2[8] & 0xFFFFFFFFL) + (n38 >>> 32);
        final long n41 = n38 & 0xFFFFFFFFL;
        final long n42 = n34 + n37 * n;
        final int n43 = (int)n42;
        array2[4] = (n43 << 1 | n31);
        final int n44 = n43 >>> 31;
        final long n45 = n36 + ((n42 >>> 32) + n37 * n9);
        final long n46 = n39 + ((n45 >>> 32) + n37 * n15);
        final long n47 = n45 & 0xFFFFFFFFL;
        final long n48 = n41 + ((n46 >>> 32) + n37 * n24);
        final long n49 = n46 & 0xFFFFFFFFL;
        final long n50 = n40 + (n48 >>> 32);
        final long n51 = n48 & 0xFFFFFFFFL;
        final long n52 = (long)array[5] & 0xFFFFFFFFL;
        final long n53 = ((long)array2[9] & 0xFFFFFFFFL) + (n50 >>> 32);
        final long n54 = n50 & 0xFFFFFFFFL;
        final long n55 = ((long)array2[10] & 0xFFFFFFFFL) + (n53 >>> 32);
        final long n56 = n53 & 0xFFFFFFFFL;
        final long n57 = n47 + n52 * n;
        final int n58 = (int)n57;
        array2[5] = (n58 << 1 | n44);
        final int n59 = n58 >>> 31;
        final long n60 = n49 + ((n57 >>> 32) + n52 * n9);
        final long n61 = n51 + ((n60 >>> 32) + n52 * n15);
        final long n62 = n60 & 0xFFFFFFFFL;
        final long n63 = n54 + ((n61 >>> 32) + n52 * n24);
        final long n64 = n61 & 0xFFFFFFFFL;
        final long n65 = n56 + ((n63 >>> 32) + n52 * n37);
        final long n66 = n63 & 0xFFFFFFFFL;
        final long n67 = n55 + (n65 >>> 32);
        final long n68 = n65 & 0xFFFFFFFFL;
        final long n69 = (long)array[6] & 0xFFFFFFFFL;
        final long n70 = ((long)array2[11] & 0xFFFFFFFFL) + (n67 >>> 32);
        final long n71 = n67 & 0xFFFFFFFFL;
        final long n72 = ((long)array2[12] & 0xFFFFFFFFL) + (n70 >>> 32);
        final long n73 = n70 & 0xFFFFFFFFL;
        final long n74 = n62 + n69 * n;
        final int n75 = (int)n74;
        array2[6] = (n75 << 1 | n59);
        final int n76 = n75 >>> 31;
        final long n77 = n64 + ((n74 >>> 32) + n69 * n9);
        final long n78 = n66 + ((n77 >>> 32) + n69 * n15);
        final long n79 = n68 + ((n78 >>> 32) + n69 * n24);
        final long n80 = n71 + ((n79 >>> 32) + n69 * n37);
        final long n81 = n73 + ((n80 >>> 32) + n69 * n52);
        final long n82 = n72 + (n81 >>> 32);
        final int n83 = (int)n77;
        array2[7] = (n83 << 1 | n76);
        final int n84 = n83 >>> 31;
        final int n85 = (int)n78;
        array2[8] = (n85 << 1 | n84);
        final int n86 = n85 >>> 31;
        final int n87 = (int)n79;
        array2[9] = (n87 << 1 | n86);
        final int n88 = n87 >>> 31;
        final int n89 = (int)n80;
        array2[10] = (n89 << 1 | n88);
        final int n90 = n89 >>> 31;
        final int n91 = (int)n81;
        array2[11] = (n91 << 1 | n90);
        final int n92 = n91 >>> 31;
        final int n93 = (int)n82;
        array2[12] = (n93 << 1 | n92);
        array2[13] = (array2[13] + (int)(n82 >>> 32) << 1 | n93 >>> 31);
    }
    
    public static void square(final int[] array, final int n, final int[] array2, final int n2) {
        final long n3 = (long)array[n + 0] & 0xFFFFFFFFL;
        int n4 = 0;
        int i = 6;
        int n5 = 14;
        do {
            final long n6 = (long)array[n + i--] & 0xFFFFFFFFL;
            final long n7 = n6 * n6;
            array2[n2 + --n5] = (n4 << 31 | (int)(n7 >>> 33));
            array2[n2 + --n5] = (int)(n7 >>> 1);
            n4 = (int)n7;
        } while (i > 0);
        final long n8 = n3 * n3;
        final long n9 = ((long)(n4 << 31) & 0xFFFFFFFFL) | n8 >>> 33;
        array2[n2 + 0] = (int)n8;
        final int n10 = (int)(n8 >>> 32) & 0x1;
        final long n11 = (long)array[n + 1] & 0xFFFFFFFFL;
        final long n12 = (long)array2[n2 + 2] & 0xFFFFFFFFL;
        final long n13 = n9 + n11 * n3;
        final int n14 = (int)n13;
        array2[n2 + 1] = (n14 << 1 | n10);
        final int n15 = n14 >>> 31;
        final long n16 = n12 + (n13 >>> 32);
        final long n17 = (long)array[n + 2] & 0xFFFFFFFFL;
        final long n18 = (long)array2[n2 + 3] & 0xFFFFFFFFL;
        final long n19 = (long)array2[n2 + 4] & 0xFFFFFFFFL;
        final long n20 = n16 + n17 * n3;
        final int n21 = (int)n20;
        array2[n2 + 2] = (n21 << 1 | n15);
        final int n22 = n21 >>> 31;
        final long n23 = n18 + ((n20 >>> 32) + n17 * n11);
        final long n24 = n19 + (n23 >>> 32);
        final long n25 = n23 & 0xFFFFFFFFL;
        final long n26 = (long)array[n + 3] & 0xFFFFFFFFL;
        final long n27 = ((long)array2[n2 + 5] & 0xFFFFFFFFL) + (n24 >>> 32);
        final long n28 = n24 & 0xFFFFFFFFL;
        final long n29 = ((long)array2[n2 + 6] & 0xFFFFFFFFL) + (n27 >>> 32);
        final long n30 = n27 & 0xFFFFFFFFL;
        final long n31 = n25 + n26 * n3;
        final int n32 = (int)n31;
        array2[n2 + 3] = (n32 << 1 | n22);
        final int n33 = n32 >>> 31;
        final long n34 = n28 + ((n31 >>> 32) + n26 * n11);
        final long n35 = n30 + ((n34 >>> 32) + n26 * n17);
        final long n36 = n34 & 0xFFFFFFFFL;
        final long n37 = n29 + (n35 >>> 32);
        final long n38 = n35 & 0xFFFFFFFFL;
        final long n39 = (long)array[n + 4] & 0xFFFFFFFFL;
        final long n40 = ((long)array2[n2 + 7] & 0xFFFFFFFFL) + (n37 >>> 32);
        final long n41 = n37 & 0xFFFFFFFFL;
        final long n42 = ((long)array2[n2 + 8] & 0xFFFFFFFFL) + (n40 >>> 32);
        final long n43 = n40 & 0xFFFFFFFFL;
        final long n44 = n36 + n39 * n3;
        final int n45 = (int)n44;
        array2[n2 + 4] = (n45 << 1 | n33);
        final int n46 = n45 >>> 31;
        final long n47 = n38 + ((n44 >>> 32) + n39 * n11);
        final long n48 = n41 + ((n47 >>> 32) + n39 * n17);
        final long n49 = n47 & 0xFFFFFFFFL;
        final long n50 = n43 + ((n48 >>> 32) + n39 * n26);
        final long n51 = n48 & 0xFFFFFFFFL;
        final long n52 = n42 + (n50 >>> 32);
        final long n53 = n50 & 0xFFFFFFFFL;
        final long n54 = (long)array[n + 5] & 0xFFFFFFFFL;
        final long n55 = ((long)array2[n2 + 9] & 0xFFFFFFFFL) + (n52 >>> 32);
        final long n56 = n52 & 0xFFFFFFFFL;
        final long n57 = ((long)array2[n2 + 10] & 0xFFFFFFFFL) + (n55 >>> 32);
        final long n58 = n55 & 0xFFFFFFFFL;
        final long n59 = n49 + n54 * n3;
        final int n60 = (int)n59;
        array2[n2 + 5] = (n60 << 1 | n46);
        final int n61 = n60 >>> 31;
        final long n62 = n51 + ((n59 >>> 32) + n54 * n11);
        final long n63 = n53 + ((n62 >>> 32) + n54 * n17);
        final long n64 = n62 & 0xFFFFFFFFL;
        final long n65 = n56 + ((n63 >>> 32) + n54 * n26);
        final long n66 = n63 & 0xFFFFFFFFL;
        final long n67 = n58 + ((n65 >>> 32) + n54 * n39);
        final long n68 = n65 & 0xFFFFFFFFL;
        final long n69 = n57 + (n67 >>> 32);
        final long n70 = n67 & 0xFFFFFFFFL;
        final long n71 = (long)array[n + 6] & 0xFFFFFFFFL;
        final long n72 = ((long)array2[n2 + 11] & 0xFFFFFFFFL) + (n69 >>> 32);
        final long n73 = n69 & 0xFFFFFFFFL;
        final long n74 = ((long)array2[n2 + 12] & 0xFFFFFFFFL) + (n72 >>> 32);
        final long n75 = n72 & 0xFFFFFFFFL;
        final long n76 = n64 + n71 * n3;
        final int n77 = (int)n76;
        array2[n2 + 6] = (n77 << 1 | n61);
        final int n78 = n77 >>> 31;
        final long n79 = n66 + ((n76 >>> 32) + n71 * n11);
        final long n80 = n68 + ((n79 >>> 32) + n71 * n17);
        final long n81 = n70 + ((n80 >>> 32) + n71 * n26);
        final long n82 = n73 + ((n81 >>> 32) + n71 * n39);
        final long n83 = n75 + ((n82 >>> 32) + n71 * n54);
        final long n84 = n74 + (n83 >>> 32);
        final int n85 = (int)n79;
        array2[n2 + 7] = (n85 << 1 | n78);
        final int n86 = n85 >>> 31;
        final int n87 = (int)n80;
        array2[n2 + 8] = (n87 << 1 | n86);
        final int n88 = n87 >>> 31;
        final int n89 = (int)n81;
        array2[n2 + 9] = (n89 << 1 | n88);
        final int n90 = n89 >>> 31;
        final int n91 = (int)n82;
        array2[n2 + 10] = (n91 << 1 | n90);
        final int n92 = n91 >>> 31;
        final int n93 = (int)n83;
        array2[n2 + 11] = (n93 << 1 | n92);
        final int n94 = n93 >>> 31;
        final int n95 = (int)n84;
        array2[n2 + 12] = (n95 << 1 | n94);
        array2[n2 + 13] = (array2[n2 + 13] + (int)(n84 >>> 32) << 1 | n95 >>> 31);
    }
    
    public static int sub(final int[] array, final int[] array2, final int[] array3) {
        final long n = 0L + (((long)array[0] & 0xFFFFFFFFL) - ((long)array2[0] & 0xFFFFFFFFL));
        array3[0] = (int)n;
        final long n2 = (n >> 32) + (((long)array[1] & 0xFFFFFFFFL) - ((long)array2[1] & 0xFFFFFFFFL));
        array3[1] = (int)n2;
        final long n3 = (n2 >> 32) + (((long)array[2] & 0xFFFFFFFFL) - ((long)array2[2] & 0xFFFFFFFFL));
        array3[2] = (int)n3;
        final long n4 = (n3 >> 32) + (((long)array[3] & 0xFFFFFFFFL) - ((long)array2[3] & 0xFFFFFFFFL));
        array3[3] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array[4] & 0xFFFFFFFFL) - ((long)array2[4] & 0xFFFFFFFFL));
        array3[4] = (int)n5;
        final long n6 = (n5 >> 32) + (((long)array[5] & 0xFFFFFFFFL) - ((long)array2[5] & 0xFFFFFFFFL));
        array3[5] = (int)n6;
        final long n7 = (n6 >> 32) + (((long)array[6] & 0xFFFFFFFFL) - ((long)array2[6] & 0xFFFFFFFFL));
        array3[6] = (int)n7;
        return (int)(n7 >> 32);
    }
    
    public static int sub(final int[] array, final int n, final int[] array2, final int n2, final int[] array3, final int n3) {
        final long n4 = 0L + (((long)array[n + 0] & 0xFFFFFFFFL) - ((long)array2[n2 + 0] & 0xFFFFFFFFL));
        array3[n3 + 0] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array[n + 1] & 0xFFFFFFFFL) - ((long)array2[n2 + 1] & 0xFFFFFFFFL));
        array3[n3 + 1] = (int)n5;
        final long n6 = (n5 >> 32) + (((long)array[n + 2] & 0xFFFFFFFFL) - ((long)array2[n2 + 2] & 0xFFFFFFFFL));
        array3[n3 + 2] = (int)n6;
        final long n7 = (n6 >> 32) + (((long)array[n + 3] & 0xFFFFFFFFL) - ((long)array2[n2 + 3] & 0xFFFFFFFFL));
        array3[n3 + 3] = (int)n7;
        final long n8 = (n7 >> 32) + (((long)array[n + 4] & 0xFFFFFFFFL) - ((long)array2[n2 + 4] & 0xFFFFFFFFL));
        array3[n3 + 4] = (int)n8;
        final long n9 = (n8 >> 32) + (((long)array[n + 5] & 0xFFFFFFFFL) - ((long)array2[n2 + 5] & 0xFFFFFFFFL));
        array3[n3 + 5] = (int)n9;
        final long n10 = (n9 >> 32) + (((long)array[n + 6] & 0xFFFFFFFFL) - ((long)array2[n2 + 6] & 0xFFFFFFFFL));
        array3[n3 + 6] = (int)n10;
        return (int)(n10 >> 32);
    }
    
    public static int subBothFrom(final int[] array, final int[] array2, final int[] array3) {
        final long n = 0L + (((long)array3[0] & 0xFFFFFFFFL) - ((long)array[0] & 0xFFFFFFFFL) - ((long)array2[0] & 0xFFFFFFFFL));
        array3[0] = (int)n;
        final long n2 = (n >> 32) + (((long)array3[1] & 0xFFFFFFFFL) - ((long)array[1] & 0xFFFFFFFFL) - ((long)array2[1] & 0xFFFFFFFFL));
        array3[1] = (int)n2;
        final long n3 = (n2 >> 32) + (((long)array3[2] & 0xFFFFFFFFL) - ((long)array[2] & 0xFFFFFFFFL) - ((long)array2[2] & 0xFFFFFFFFL));
        array3[2] = (int)n3;
        final long n4 = (n3 >> 32) + (((long)array3[3] & 0xFFFFFFFFL) - ((long)array[3] & 0xFFFFFFFFL) - ((long)array2[3] & 0xFFFFFFFFL));
        array3[3] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array3[4] & 0xFFFFFFFFL) - ((long)array[4] & 0xFFFFFFFFL) - ((long)array2[4] & 0xFFFFFFFFL));
        array3[4] = (int)n5;
        final long n6 = (n5 >> 32) + (((long)array3[5] & 0xFFFFFFFFL) - ((long)array[5] & 0xFFFFFFFFL) - ((long)array2[5] & 0xFFFFFFFFL));
        array3[5] = (int)n6;
        final long n7 = (n6 >> 32) + (((long)array3[6] & 0xFFFFFFFFL) - ((long)array[6] & 0xFFFFFFFFL) - ((long)array2[6] & 0xFFFFFFFFL));
        array3[6] = (int)n7;
        return (int)(n7 >> 32);
    }
    
    public static int subFrom(final int[] array, final int[] array2) {
        final long n = 0L + (((long)array2[0] & 0xFFFFFFFFL) - ((long)array[0] & 0xFFFFFFFFL));
        array2[0] = (int)n;
        final long n2 = (n >> 32) + (((long)array2[1] & 0xFFFFFFFFL) - ((long)array[1] & 0xFFFFFFFFL));
        array2[1] = (int)n2;
        final long n3 = (n2 >> 32) + (((long)array2[2] & 0xFFFFFFFFL) - ((long)array[2] & 0xFFFFFFFFL));
        array2[2] = (int)n3;
        final long n4 = (n3 >> 32) + (((long)array2[3] & 0xFFFFFFFFL) - ((long)array[3] & 0xFFFFFFFFL));
        array2[3] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array2[4] & 0xFFFFFFFFL) - ((long)array[4] & 0xFFFFFFFFL));
        array2[4] = (int)n5;
        final long n6 = (n5 >> 32) + (((long)array2[5] & 0xFFFFFFFFL) - ((long)array[5] & 0xFFFFFFFFL));
        array2[5] = (int)n6;
        final long n7 = (n6 >> 32) + (((long)array2[6] & 0xFFFFFFFFL) - ((long)array[6] & 0xFFFFFFFFL));
        array2[6] = (int)n7;
        return (int)(n7 >> 32);
    }
    
    public static int subFrom(final int[] array, final int n, final int[] array2, final int n2) {
        final long n3 = 0L + (((long)array2[n2 + 0] & 0xFFFFFFFFL) - ((long)array[n + 0] & 0xFFFFFFFFL));
        array2[n2 + 0] = (int)n3;
        final long n4 = (n3 >> 32) + (((long)array2[n2 + 1] & 0xFFFFFFFFL) - ((long)array[n + 1] & 0xFFFFFFFFL));
        array2[n2 + 1] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array2[n2 + 2] & 0xFFFFFFFFL) - ((long)array[n + 2] & 0xFFFFFFFFL));
        array2[n2 + 2] = (int)n5;
        final long n6 = (n5 >> 32) + (((long)array2[n2 + 3] & 0xFFFFFFFFL) - ((long)array[n + 3] & 0xFFFFFFFFL));
        array2[n2 + 3] = (int)n6;
        final long n7 = (n6 >> 32) + (((long)array2[n2 + 4] & 0xFFFFFFFFL) - ((long)array[n + 4] & 0xFFFFFFFFL));
        array2[n2 + 4] = (int)n7;
        final long n8 = (n7 >> 32) + (((long)array2[n2 + 5] & 0xFFFFFFFFL) - ((long)array[n + 5] & 0xFFFFFFFFL));
        array2[n2 + 5] = (int)n8;
        final long n9 = (n8 >> 32) + (((long)array2[n2 + 6] & 0xFFFFFFFFL) - ((long)array[n + 6] & 0xFFFFFFFFL));
        array2[n2 + 6] = (int)n9;
        return (int)(n9 >> 32);
    }
    
    public static BigInteger toBigInteger(final int[] array) {
        final byte[] array2 = new byte[28];
        for (int i = 0; i < 7; ++i) {
            final int n = array[i];
            if (n != 0) {
                Pack.intToBigEndian(n, array2, 6 - i << 2);
            }
        }
        return new BigInteger(1, array2);
    }
    
    public static void zero(final int[] array) {
        array[0] = 0;
        array[2] = (array[1] = 0);
        array[4] = (array[3] = 0);
        array[6] = (array[5] = 0);
    }
}
