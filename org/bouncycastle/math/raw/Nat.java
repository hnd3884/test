package org.bouncycastle.math.raw;

import org.bouncycastle.util.Pack;
import java.math.BigInteger;

public abstract class Nat
{
    private static final long M = 4294967295L;
    
    public static int add(final int n, final int[] array, final int[] array2, final int[] array3) {
        long n2 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n3 = n2 + (((long)array[i] & 0xFFFFFFFFL) + ((long)array2[i] & 0xFFFFFFFFL));
            array3[i] = (int)n3;
            n2 = n3 >>> 32;
        }
        return (int)n2;
    }
    
    public static int add33At(final int n, final int n2, final int[] array, final int n3) {
        final long n4 = ((long)array[n3 + 0] & 0xFFFFFFFFL) + ((long)n2 & 0xFFFFFFFFL);
        array[n3 + 0] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[n3 + 1] & 0xFFFFFFFFL) + 1L);
        array[n3 + 1] = (int)n5;
        return (n5 >>> 32 == 0L) ? 0 : incAt(n, array, n3 + 2);
    }
    
    public static int add33At(final int n, final int n2, final int[] array, final int n3, final int n4) {
        final long n5 = ((long)array[n3 + n4] & 0xFFFFFFFFL) + ((long)n2 & 0xFFFFFFFFL);
        array[n3 + n4] = (int)n5;
        final long n6 = (n5 >>> 32) + (((long)array[n3 + n4 + 1] & 0xFFFFFFFFL) + 1L);
        array[n3 + n4 + 1] = (int)n6;
        return (n6 >>> 32 == 0L) ? 0 : incAt(n, array, n3, n4 + 2);
    }
    
    public static int add33To(final int n, final int n2, final int[] array) {
        final long n3 = ((long)array[0] & 0xFFFFFFFFL) + ((long)n2 & 0xFFFFFFFFL);
        array[0] = (int)n3;
        final long n4 = (n3 >>> 32) + (((long)array[1] & 0xFFFFFFFFL) + 1L);
        array[1] = (int)n4;
        return (n4 >>> 32 == 0L) ? 0 : incAt(n, array, 2);
    }
    
    public static int add33To(final int n, final int n2, final int[] array, final int n3) {
        final long n4 = ((long)array[n3 + 0] & 0xFFFFFFFFL) + ((long)n2 & 0xFFFFFFFFL);
        array[n3 + 0] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[n3 + 1] & 0xFFFFFFFFL) + 1L);
        array[n3 + 1] = (int)n5;
        return (n5 >>> 32 == 0L) ? 0 : incAt(n, array, n3, 2);
    }
    
    public static int addBothTo(final int n, final int[] array, final int[] array2, final int[] array3) {
        long n2 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n3 = n2 + (((long)array[i] & 0xFFFFFFFFL) + ((long)array2[i] & 0xFFFFFFFFL) + ((long)array3[i] & 0xFFFFFFFFL));
            array3[i] = (int)n3;
            n2 = n3 >>> 32;
        }
        return (int)n2;
    }
    
    public static int addBothTo(final int n, final int[] array, final int n2, final int[] array2, final int n3, final int[] array3, final int n4) {
        long n5 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n6 = n5 + (((long)array[n2 + i] & 0xFFFFFFFFL) + ((long)array2[n3 + i] & 0xFFFFFFFFL) + ((long)array3[n4 + i] & 0xFFFFFFFFL));
            array3[n4 + i] = (int)n6;
            n5 = n6 >>> 32;
        }
        return (int)n5;
    }
    
    public static int addDWordAt(final int n, final long n2, final int[] array, final int n3) {
        final long n4 = ((long)array[n3 + 0] & 0xFFFFFFFFL) + (n2 & 0xFFFFFFFFL);
        array[n3 + 0] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[n3 + 1] & 0xFFFFFFFFL) + (n2 >>> 32));
        array[n3 + 1] = (int)n5;
        return (n5 >>> 32 == 0L) ? 0 : incAt(n, array, n3 + 2);
    }
    
    public static int addDWordAt(final int n, final long n2, final int[] array, final int n3, final int n4) {
        final long n5 = ((long)array[n3 + n4] & 0xFFFFFFFFL) + (n2 & 0xFFFFFFFFL);
        array[n3 + n4] = (int)n5;
        final long n6 = (n5 >>> 32) + (((long)array[n3 + n4 + 1] & 0xFFFFFFFFL) + (n2 >>> 32));
        array[n3 + n4 + 1] = (int)n6;
        return (n6 >>> 32 == 0L) ? 0 : incAt(n, array, n3, n4 + 2);
    }
    
    public static int addDWordTo(final int n, final long n2, final int[] array) {
        final long n3 = ((long)array[0] & 0xFFFFFFFFL) + (n2 & 0xFFFFFFFFL);
        array[0] = (int)n3;
        final long n4 = (n3 >>> 32) + (((long)array[1] & 0xFFFFFFFFL) + (n2 >>> 32));
        array[1] = (int)n4;
        return (n4 >>> 32 == 0L) ? 0 : incAt(n, array, 2);
    }
    
    public static int addDWordTo(final int n, final long n2, final int[] array, final int n3) {
        final long n4 = ((long)array[n3 + 0] & 0xFFFFFFFFL) + (n2 & 0xFFFFFFFFL);
        array[n3 + 0] = (int)n4;
        final long n5 = (n4 >>> 32) + (((long)array[n3 + 1] & 0xFFFFFFFFL) + (n2 >>> 32));
        array[n3 + 1] = (int)n5;
        return (n5 >>> 32 == 0L) ? 0 : incAt(n, array, n3, 2);
    }
    
    public static int addTo(final int n, final int[] array, final int[] array2) {
        long n2 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n3 = n2 + (((long)array[i] & 0xFFFFFFFFL) + ((long)array2[i] & 0xFFFFFFFFL));
            array2[i] = (int)n3;
            n2 = n3 >>> 32;
        }
        return (int)n2;
    }
    
    public static int addTo(final int n, final int[] array, final int n2, final int[] array2, final int n3) {
        long n4 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n5 = n4 + (((long)array[n2 + i] & 0xFFFFFFFFL) + ((long)array2[n3 + i] & 0xFFFFFFFFL));
            array2[n3 + i] = (int)n5;
            n4 = n5 >>> 32;
        }
        return (int)n4;
    }
    
    public static int addWordAt(final int n, final int n2, final int[] array, final int n3) {
        final long n4 = ((long)n2 & 0xFFFFFFFFL) + ((long)array[n3] & 0xFFFFFFFFL);
        array[n3] = (int)n4;
        return (n4 >>> 32 == 0L) ? 0 : incAt(n, array, n3 + 1);
    }
    
    public static int addWordAt(final int n, final int n2, final int[] array, final int n3, final int n4) {
        final long n5 = ((long)n2 & 0xFFFFFFFFL) + ((long)array[n3 + n4] & 0xFFFFFFFFL);
        array[n3 + n4] = (int)n5;
        return (n5 >>> 32 == 0L) ? 0 : incAt(n, array, n3, n4 + 1);
    }
    
    public static int addWordTo(final int n, final int n2, final int[] array) {
        final long n3 = ((long)n2 & 0xFFFFFFFFL) + ((long)array[0] & 0xFFFFFFFFL);
        array[0] = (int)n3;
        return (n3 >>> 32 == 0L) ? 0 : incAt(n, array, 1);
    }
    
    public static int addWordTo(final int n, final int n2, final int[] array, final int n3) {
        final long n4 = ((long)n2 & 0xFFFFFFFFL) + ((long)array[n3] & 0xFFFFFFFFL);
        array[n3] = (int)n4;
        return (n4 >>> 32 == 0L) ? 0 : incAt(n, array, n3, 1);
    }
    
    public static int[] copy(final int n, final int[] array) {
        final int[] array2 = new int[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    public static void copy(final int n, final int[] array, final int[] array2) {
        System.arraycopy(array, 0, array2, 0, n);
    }
    
    public static void copy(final int n, final int[] array, final int n2, final int[] array2, final int n3) {
        System.arraycopy(array, n2, array2, n3, n);
    }
    
    public static int[] create(final int n) {
        return new int[n];
    }
    
    public static long[] create64(final int n) {
        return new long[n];
    }
    
    public static int dec(final int n, final int[] array) {
        for (int i = 0; i < n; ++i) {
            final int n2 = i;
            if (--array[n2] != -1) {
                return 0;
            }
        }
        return -1;
    }
    
    public static int dec(final int n, final int[] array, final int[] array2) {
        int i = 0;
        while (i < n) {
            final int n2 = array[i] - 1;
            array2[i] = n2;
            ++i;
            if (n2 != -1) {
                while (i < n) {
                    array2[i] = array[i];
                    ++i;
                }
                return 0;
            }
        }
        return -1;
    }
    
    public static int decAt(final int n, final int[] array, final int n2) {
        for (int i = n2; i < n; ++i) {
            final int n3 = i;
            if (--array[n3] != -1) {
                return 0;
            }
        }
        return -1;
    }
    
    public static int decAt(final int n, final int[] array, final int n2, final int n3) {
        for (int i = n3; i < n; ++i) {
            final int n4 = n2 + i;
            if (--array[n4] != -1) {
                return 0;
            }
        }
        return -1;
    }
    
    public static boolean eq(final int n, final int[] array, final int[] array2) {
        for (int i = n - 1; i >= 0; --i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static int[] fromBigInteger(final int n, BigInteger shiftRight) {
        if (shiftRight.signum() < 0 || shiftRight.bitLength() > n) {
            throw new IllegalArgumentException();
        }
        final int[] create = create(n + 31 >> 5);
        int n2 = 0;
        while (shiftRight.signum() != 0) {
            create[n2++] = shiftRight.intValue();
            shiftRight = shiftRight.shiftRight(32);
        }
        return create;
    }
    
    public static int getBit(final int[] array, final int n) {
        if (n == 0) {
            return array[0] & 0x1;
        }
        final int n2 = n >> 5;
        if (n2 < 0 || n2 >= array.length) {
            return 0;
        }
        return array[n2] >>> (n & 0x1F) & 0x1;
    }
    
    public static boolean gte(final int n, final int[] array, final int[] array2) {
        for (int i = n - 1; i >= 0; --i) {
            final int n2 = array[i] ^ Integer.MIN_VALUE;
            final int n3 = array2[i] ^ Integer.MIN_VALUE;
            if (n2 < n3) {
                return false;
            }
            if (n2 > n3) {
                return true;
            }
        }
        return true;
    }
    
    public static int inc(final int n, final int[] array) {
        for (int i = 0; i < n; ++i) {
            if (++array[i] != 0) {
                return 0;
            }
        }
        return 1;
    }
    
    public static int inc(final int n, final int[] array, final int[] array2) {
        int i = 0;
        while (i < n) {
            final int n2 = array[i] + 1;
            array2[i] = n2;
            ++i;
            if (n2 != 0) {
                while (i < n) {
                    array2[i] = array[i];
                    ++i;
                }
                return 0;
            }
        }
        return 1;
    }
    
    public static int incAt(final int n, final int[] array, final int n2) {
        for (int i = n2; i < n; ++i) {
            if (++array[i] != 0) {
                return 0;
            }
        }
        return 1;
    }
    
    public static int incAt(final int n, final int[] array, final int n2, final int n3) {
        for (int i = n3; i < n; ++i) {
            if (++array[n2 + i] != 0) {
                return 0;
            }
        }
        return 1;
    }
    
    public static boolean isOne(final int n, final int[] array) {
        if (array[0] != 1) {
            return false;
        }
        for (int i = 1; i < n; ++i) {
            if (array[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isZero(final int n, final int[] array) {
        for (int i = 0; i < n; ++i) {
            if (array[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    public static void mul(final int n, final int[] array, final int[] array2, final int[] array3) {
        array3[n] = mulWord(n, array[0], array2, array3);
        for (int i = 1; i < n; ++i) {
            array3[i + n] = mulWordAddTo(n, array[i], array2, 0, array3, i);
        }
    }
    
    public static void mul(final int n, final int[] array, final int n2, final int[] array2, final int n3, final int[] array3, final int n4) {
        array3[n4 + n] = mulWord(n, array[n2], array2, n3, array3, n4);
        for (int i = 1; i < n; ++i) {
            array3[n4 + i + n] = mulWordAddTo(n, array[n2 + i], array2, n3, array3, n4 + i);
        }
    }
    
    public static int mulAddTo(final int n, final int[] array, final int[] array2, final int[] array3) {
        long n2 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n3 = ((long)mulWordAddTo(n, array[i], array2, 0, array3, i) & 0xFFFFFFFFL) + (n2 + ((long)array3[i + n] & 0xFFFFFFFFL));
            array3[i + n] = (int)n3;
            n2 = n3 >>> 32;
        }
        return (int)n2;
    }
    
    public static int mulAddTo(final int n, final int[] array, final int n2, final int[] array2, final int n3, final int[] array3, int n4) {
        long n5 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n6 = ((long)mulWordAddTo(n, array[n2 + i], array2, n3, array3, n4) & 0xFFFFFFFFL) + (n5 + ((long)array3[n4 + n] & 0xFFFFFFFFL));
            array3[n4 + n] = (int)n6;
            n5 = n6 >>> 32;
            ++n4;
        }
        return (int)n5;
    }
    
    public static int mul31BothAdd(final int n, final int n2, final int[] array, final int n3, final int[] array2, final int[] array3, final int n4) {
        long n5 = 0L;
        final long n6 = (long)n2 & 0xFFFFFFFFL;
        final long n7 = (long)n3 & 0xFFFFFFFFL;
        int n8 = 0;
        do {
            final long n9 = n5 + (n6 * ((long)array[n8] & 0xFFFFFFFFL) + n7 * ((long)array2[n8] & 0xFFFFFFFFL) + ((long)array3[n4 + n8] & 0xFFFFFFFFL));
            array3[n4 + n8] = (int)n9;
            n5 = n9 >>> 32;
        } while (++n8 < n);
        return (int)n5;
    }
    
    public static int mulWord(final int n, final int n2, final int[] array, final int[] array2) {
        long n3 = 0L;
        final long n4 = (long)n2 & 0xFFFFFFFFL;
        int n5 = 0;
        do {
            final long n6 = n3 + n4 * ((long)array[n5] & 0xFFFFFFFFL);
            array2[n5] = (int)n6;
            n3 = n6 >>> 32;
        } while (++n5 < n);
        return (int)n3;
    }
    
    public static int mulWord(final int n, final int n2, final int[] array, final int n3, final int[] array2, final int n4) {
        long n5 = 0L;
        final long n6 = (long)n2 & 0xFFFFFFFFL;
        int n7 = 0;
        do {
            final long n8 = n5 + n6 * ((long)array[n3 + n7] & 0xFFFFFFFFL);
            array2[n4 + n7] = (int)n8;
            n5 = n8 >>> 32;
        } while (++n7 < n);
        return (int)n5;
    }
    
    public static int mulWordAddTo(final int n, final int n2, final int[] array, final int n3, final int[] array2, final int n4) {
        long n5 = 0L;
        final long n6 = (long)n2 & 0xFFFFFFFFL;
        int n7 = 0;
        do {
            final long n8 = n5 + (n6 * ((long)array[n3 + n7] & 0xFFFFFFFFL) + ((long)array2[n4 + n7] & 0xFFFFFFFFL));
            array2[n4 + n7] = (int)n8;
            n5 = n8 >>> 32;
        } while (++n7 < n);
        return (int)n5;
    }
    
    public static int mulWordDwordAddAt(final int n, final int n2, final long n3, final int[] array, final int n4) {
        final long n5 = 0L;
        final long n6 = (long)n2 & 0xFFFFFFFFL;
        final long n7 = n5 + (n6 * (n3 & 0xFFFFFFFFL) + ((long)array[n4 + 0] & 0xFFFFFFFFL));
        array[n4 + 0] = (int)n7;
        final long n8 = (n7 >>> 32) + (n6 * (n3 >>> 32) + ((long)array[n4 + 1] & 0xFFFFFFFFL));
        array[n4 + 1] = (int)n8;
        final long n9 = (n8 >>> 32) + ((long)array[n4 + 2] & 0xFFFFFFFFL);
        array[n4 + 2] = (int)n9;
        return (n9 >>> 32 == 0L) ? 0 : incAt(n, array, n4 + 3);
    }
    
    public static int shiftDownBit(final int n, final int[] array, int n2) {
        int n3 = n;
        while (--n3 >= 0) {
            final int n4 = array[n3];
            array[n3] = (n4 >>> 1 | n2 << 31);
            n2 = n4;
        }
        return n2 << 31;
    }
    
    public static int shiftDownBit(final int n, final int[] array, final int n2, int n3) {
        int n4 = n;
        while (--n4 >= 0) {
            final int n5 = array[n2 + n4];
            array[n2 + n4] = (n5 >>> 1 | n3 << 31);
            n3 = n5;
        }
        return n3 << 31;
    }
    
    public static int shiftDownBit(final int n, final int[] array, int n2, final int[] array2) {
        int n3 = n;
        while (--n3 >= 0) {
            final int n4 = array[n3];
            array2[n3] = (n4 >>> 1 | n2 << 31);
            n2 = n4;
        }
        return n2 << 31;
    }
    
    public static int shiftDownBit(final int n, final int[] array, final int n2, int n3, final int[] array2, final int n4) {
        int n5 = n;
        while (--n5 >= 0) {
            final int n6 = array[n2 + n5];
            array2[n4 + n5] = (n6 >>> 1 | n3 << 31);
            n3 = n6;
        }
        return n3 << 31;
    }
    
    public static int shiftDownBits(final int n, final int[] array, final int n2, int n3) {
        int n4 = n;
        while (--n4 >= 0) {
            final int n5 = array[n4];
            array[n4] = (n5 >>> n2 | n3 << -n2);
            n3 = n5;
        }
        return n3 << -n2;
    }
    
    public static int shiftDownBits(final int n, final int[] array, final int n2, final int n3, int n4) {
        int n5 = n;
        while (--n5 >= 0) {
            final int n6 = array[n2 + n5];
            array[n2 + n5] = (n6 >>> n3 | n4 << -n3);
            n4 = n6;
        }
        return n4 << -n3;
    }
    
    public static int shiftDownBits(final int n, final int[] array, final int n2, int n3, final int[] array2) {
        int n4 = n;
        while (--n4 >= 0) {
            final int n5 = array[n4];
            array2[n4] = (n5 >>> n2 | n3 << -n2);
            n3 = n5;
        }
        return n3 << -n2;
    }
    
    public static int shiftDownBits(final int n, final int[] array, final int n2, final int n3, int n4, final int[] array2, final int n5) {
        int n6 = n;
        while (--n6 >= 0) {
            final int n7 = array[n2 + n6];
            array2[n5 + n6] = (n7 >>> n3 | n4 << -n3);
            n4 = n7;
        }
        return n4 << -n3;
    }
    
    public static int shiftDownWord(final int n, final int[] array, int n2) {
        int n3 = n;
        while (--n3 >= 0) {
            final int n4 = array[n3];
            array[n3] = n2;
            n2 = n4;
        }
        return n2;
    }
    
    public static int shiftUpBit(final int n, final int[] array, int n2) {
        for (int i = 0; i < n; ++i) {
            final int n3 = array[i];
            array[i] = (n3 << 1 | n2 >>> 31);
            n2 = n3;
        }
        return n2 >>> 31;
    }
    
    public static int shiftUpBit(final int n, final int[] array, final int n2, int n3) {
        for (int i = 0; i < n; ++i) {
            final int n4 = array[n2 + i];
            array[n2 + i] = (n4 << 1 | n3 >>> 31);
            n3 = n4;
        }
        return n3 >>> 31;
    }
    
    public static int shiftUpBit(final int n, final int[] array, int n2, final int[] array2) {
        for (int i = 0; i < n; ++i) {
            final int n3 = array[i];
            array2[i] = (n3 << 1 | n2 >>> 31);
            n2 = n3;
        }
        return n2 >>> 31;
    }
    
    public static int shiftUpBit(final int n, final int[] array, final int n2, int n3, final int[] array2, final int n4) {
        for (int i = 0; i < n; ++i) {
            final int n5 = array[n2 + i];
            array2[n4 + i] = (n5 << 1 | n3 >>> 31);
            n3 = n5;
        }
        return n3 >>> 31;
    }
    
    public static long shiftUpBit64(final int n, final long[] array, final int n2, long n3, final long[] array2, final int n4) {
        for (int i = 0; i < n; ++i) {
            final long n5 = array[n2 + i];
            array2[n4 + i] = (n5 << 1 | n3 >>> 63);
            n3 = n5;
        }
        return n3 >>> 63;
    }
    
    public static int shiftUpBits(final int n, final int[] array, final int n2, int n3) {
        for (int i = 0; i < n; ++i) {
            final int n4 = array[i];
            array[i] = (n4 << n2 | n3 >>> -n2);
            n3 = n4;
        }
        return n3 >>> -n2;
    }
    
    public static int shiftUpBits(final int n, final int[] array, final int n2, final int n3, int n4) {
        for (int i = 0; i < n; ++i) {
            final int n5 = array[n2 + i];
            array[n2 + i] = (n5 << n3 | n4 >>> -n3);
            n4 = n5;
        }
        return n4 >>> -n3;
    }
    
    public static long shiftUpBits64(final int n, final long[] array, final int n2, final int n3, long n4) {
        for (int i = 0; i < n; ++i) {
            final long n5 = array[n2 + i];
            array[n2 + i] = (n5 << n3 | n4 >>> -n3);
            n4 = n5;
        }
        return n4 >>> -n3;
    }
    
    public static int shiftUpBits(final int n, final int[] array, final int n2, int n3, final int[] array2) {
        for (int i = 0; i < n; ++i) {
            final int n4 = array[i];
            array2[i] = (n4 << n2 | n3 >>> -n2);
            n3 = n4;
        }
        return n3 >>> -n2;
    }
    
    public static int shiftUpBits(final int n, final int[] array, final int n2, final int n3, int n4, final int[] array2, final int n5) {
        for (int i = 0; i < n; ++i) {
            final int n6 = array[n2 + i];
            array2[n5 + i] = (n6 << n3 | n4 >>> -n3);
            n4 = n6;
        }
        return n4 >>> -n3;
    }
    
    public static long shiftUpBits64(final int n, final long[] array, final int n2, final int n3, long n4, final long[] array2, final int n5) {
        for (int i = 0; i < n; ++i) {
            final long n6 = array[n2 + i];
            array2[n5 + i] = (n6 << n3 | n4 >>> -n3);
            n4 = n6;
        }
        return n4 >>> -n3;
    }
    
    public static void square(final int n, final int[] array, final int[] array2) {
        final int n2 = n << 1;
        int n3 = 0;
        int i = n;
        int n4 = n2;
        do {
            final long n5 = (long)array[--i] & 0xFFFFFFFFL;
            final long n6 = n5 * n5;
            array2[--n4] = (n3 << 31 | (int)(n6 >>> 33));
            array2[--n4] = (int)(n6 >>> 1);
            n3 = (int)n6;
        } while (i > 0);
        for (int j = 1; j < n; ++j) {
            addWordAt(n2, squareWordAdd(array, j, array2), array2, j << 1);
        }
        shiftUpBit(n2, array2, array[0] << 31);
    }
    
    public static void square(final int n, final int[] array, final int n2, final int[] array2, final int n3) {
        final int n4 = n << 1;
        int n5 = 0;
        int i = n;
        int n6 = n4;
        do {
            final long n7 = (long)array[n2 + --i] & 0xFFFFFFFFL;
            final long n8 = n7 * n7;
            array2[n3 + --n6] = (n5 << 31 | (int)(n8 >>> 33));
            array2[n3 + --n6] = (int)(n8 >>> 1);
            n5 = (int)n8;
        } while (i > 0);
        for (int j = 1; j < n; ++j) {
            addWordAt(n4, squareWordAdd(array, n2, j, array2, n3), array2, n3, j << 1);
        }
        shiftUpBit(n4, array2, n3, array[n2] << 31);
    }
    
    public static int squareWordAdd(final int[] array, final int n, final int[] array2) {
        long n2 = 0L;
        final long n3 = (long)array[n] & 0xFFFFFFFFL;
        int n4 = 0;
        do {
            final long n5 = n2 + (n3 * ((long)array[n4] & 0xFFFFFFFFL) + ((long)array2[n + n4] & 0xFFFFFFFFL));
            array2[n + n4] = (int)n5;
            n2 = n5 >>> 32;
        } while (++n4 < n);
        return (int)n2;
    }
    
    public static int squareWordAdd(final int[] array, final int n, final int n2, final int[] array2, int n3) {
        long n4 = 0L;
        final long n5 = (long)array[n + n2] & 0xFFFFFFFFL;
        int n6 = 0;
        do {
            final long n7 = n4 + (n5 * ((long)array[n + n6] & 0xFFFFFFFFL) + ((long)array2[n2 + n3] & 0xFFFFFFFFL));
            array2[n2 + n3] = (int)n7;
            n4 = n7 >>> 32;
            ++n3;
        } while (++n6 < n2);
        return (int)n4;
    }
    
    public static int sub(final int n, final int[] array, final int[] array2, final int[] array3) {
        long n2 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n3 = n2 + (((long)array[i] & 0xFFFFFFFFL) - ((long)array2[i] & 0xFFFFFFFFL));
            array3[i] = (int)n3;
            n2 = n3 >> 32;
        }
        return (int)n2;
    }
    
    public static int sub(final int n, final int[] array, final int n2, final int[] array2, final int n3, final int[] array3, final int n4) {
        long n5 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n6 = n5 + (((long)array[n2 + i] & 0xFFFFFFFFL) - ((long)array2[n3 + i] & 0xFFFFFFFFL));
            array3[n4 + i] = (int)n6;
            n5 = n6 >> 32;
        }
        return (int)n5;
    }
    
    public static int sub33At(final int n, final int n2, final int[] array, final int n3) {
        final long n4 = ((long)array[n3 + 0] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        array[n3 + 0] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array[n3 + 1] & 0xFFFFFFFFL) - 1L);
        array[n3 + 1] = (int)n5;
        return (n5 >> 32 == 0L) ? 0 : decAt(n, array, n3 + 2);
    }
    
    public static int sub33At(final int n, final int n2, final int[] array, final int n3, final int n4) {
        final long n5 = ((long)array[n3 + n4] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        array[n3 + n4] = (int)n5;
        final long n6 = (n5 >> 32) + (((long)array[n3 + n4 + 1] & 0xFFFFFFFFL) - 1L);
        array[n3 + n4 + 1] = (int)n6;
        return (n6 >> 32 == 0L) ? 0 : decAt(n, array, n3, n4 + 2);
    }
    
    public static int sub33From(final int n, final int n2, final int[] array) {
        final long n3 = ((long)array[0] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        array[0] = (int)n3;
        final long n4 = (n3 >> 32) + (((long)array[1] & 0xFFFFFFFFL) - 1L);
        array[1] = (int)n4;
        return (n4 >> 32 == 0L) ? 0 : decAt(n, array, 2);
    }
    
    public static int sub33From(final int n, final int n2, final int[] array, final int n3) {
        final long n4 = ((long)array[n3 + 0] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        array[n3 + 0] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array[n3 + 1] & 0xFFFFFFFFL) - 1L);
        array[n3 + 1] = (int)n5;
        return (n5 >> 32 == 0L) ? 0 : decAt(n, array, n3, 2);
    }
    
    public static int subBothFrom(final int n, final int[] array, final int[] array2, final int[] array3) {
        long n2 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n3 = n2 + (((long)array3[i] & 0xFFFFFFFFL) - ((long)array[i] & 0xFFFFFFFFL) - ((long)array2[i] & 0xFFFFFFFFL));
            array3[i] = (int)n3;
            n2 = n3 >> 32;
        }
        return (int)n2;
    }
    
    public static int subBothFrom(final int n, final int[] array, final int n2, final int[] array2, final int n3, final int[] array3, final int n4) {
        long n5 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n6 = n5 + (((long)array3[n4 + i] & 0xFFFFFFFFL) - ((long)array[n2 + i] & 0xFFFFFFFFL) - ((long)array2[n3 + i] & 0xFFFFFFFFL));
            array3[n4 + i] = (int)n6;
            n5 = n6 >> 32;
        }
        return (int)n5;
    }
    
    public static int subDWordAt(final int n, final long n2, final int[] array, final int n3) {
        final long n4 = ((long)array[n3 + 0] & 0xFFFFFFFFL) - (n2 & 0xFFFFFFFFL);
        array[n3 + 0] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array[n3 + 1] & 0xFFFFFFFFL) - (n2 >>> 32));
        array[n3 + 1] = (int)n5;
        return (n5 >> 32 == 0L) ? 0 : decAt(n, array, n3 + 2);
    }
    
    public static int subDWordAt(final int n, final long n2, final int[] array, final int n3, final int n4) {
        final long n5 = ((long)array[n3 + n4] & 0xFFFFFFFFL) - (n2 & 0xFFFFFFFFL);
        array[n3 + n4] = (int)n5;
        final long n6 = (n5 >> 32) + (((long)array[n3 + n4 + 1] & 0xFFFFFFFFL) - (n2 >>> 32));
        array[n3 + n4 + 1] = (int)n6;
        return (n6 >> 32 == 0L) ? 0 : decAt(n, array, n3, n4 + 2);
    }
    
    public static int subDWordFrom(final int n, final long n2, final int[] array) {
        final long n3 = ((long)array[0] & 0xFFFFFFFFL) - (n2 & 0xFFFFFFFFL);
        array[0] = (int)n3;
        final long n4 = (n3 >> 32) + (((long)array[1] & 0xFFFFFFFFL) - (n2 >>> 32));
        array[1] = (int)n4;
        return (n4 >> 32 == 0L) ? 0 : decAt(n, array, 2);
    }
    
    public static int subDWordFrom(final int n, final long n2, final int[] array, final int n3) {
        final long n4 = ((long)array[n3 + 0] & 0xFFFFFFFFL) - (n2 & 0xFFFFFFFFL);
        array[n3 + 0] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array[n3 + 1] & 0xFFFFFFFFL) - (n2 >>> 32));
        array[n3 + 1] = (int)n5;
        return (n5 >> 32 == 0L) ? 0 : decAt(n, array, n3, 2);
    }
    
    public static int subFrom(final int n, final int[] array, final int[] array2) {
        long n2 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n3 = n2 + (((long)array2[i] & 0xFFFFFFFFL) - ((long)array[i] & 0xFFFFFFFFL));
            array2[i] = (int)n3;
            n2 = n3 >> 32;
        }
        return (int)n2;
    }
    
    public static int subFrom(final int n, final int[] array, final int n2, final int[] array2, final int n3) {
        long n4 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n5 = n4 + (((long)array2[n3 + i] & 0xFFFFFFFFL) - ((long)array[n2 + i] & 0xFFFFFFFFL));
            array2[n3 + i] = (int)n5;
            n4 = n5 >> 32;
        }
        return (int)n4;
    }
    
    public static int subWordAt(final int n, final int n2, final int[] array, final int n3) {
        final long n4 = ((long)array[n3] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        array[n3] = (int)n4;
        return (n4 >> 32 == 0L) ? 0 : decAt(n, array, n3 + 1);
    }
    
    public static int subWordAt(final int n, final int n2, final int[] array, final int n3, final int n4) {
        final long n5 = ((long)array[n3 + n4] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        array[n3 + n4] = (int)n5;
        return (n5 >> 32 == 0L) ? 0 : decAt(n, array, n3, n4 + 1);
    }
    
    public static int subWordFrom(final int n, final int n2, final int[] array) {
        final long n3 = ((long)array[0] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        array[0] = (int)n3;
        return (n3 >> 32 == 0L) ? 0 : decAt(n, array, 1);
    }
    
    public static int subWordFrom(final int n, final int n2, final int[] array, final int n3) {
        final long n4 = ((long)array[n3 + 0] & 0xFFFFFFFFL) - ((long)n2 & 0xFFFFFFFFL);
        array[n3 + 0] = (int)n4;
        return (n4 >> 32 == 0L) ? 0 : decAt(n, array, n3, 1);
    }
    
    public static BigInteger toBigInteger(final int n, final int[] array) {
        final byte[] array2 = new byte[n << 2];
        for (int i = 0; i < n; ++i) {
            final int n2 = array[i];
            if (n2 != 0) {
                Pack.intToBigEndian(n2, array2, n - 1 - i << 2);
            }
        }
        return new BigInteger(1, array2);
    }
    
    public static void zero(final int n, final int[] array) {
        for (int i = 0; i < n; ++i) {
            array[i] = 0;
        }
    }
    
    public static void zero64(final int n, final long[] array) {
        for (int i = 0; i < n; ++i) {
            array[i] = 0L;
        }
    }
}
