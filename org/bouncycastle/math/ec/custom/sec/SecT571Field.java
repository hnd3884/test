package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat576;
import java.math.BigInteger;

public class SecT571Field
{
    private static final long M59 = 576460752303423487L;
    private static final long RM = -1190112520884487202L;
    private static final long[] ROOT_Z;
    
    public static void add(final long[] array, final long[] array2, final long[] array3) {
        for (int i = 0; i < 9; ++i) {
            array3[i] = (array[i] ^ array2[i]);
        }
    }
    
    private static void add(final long[] array, final int n, final long[] array2, final int n2, final long[] array3, final int n3) {
        for (int i = 0; i < 9; ++i) {
            array3[n3 + i] = (array[n + i] ^ array2[n2 + i]);
        }
    }
    
    public static void addBothTo(final long[] array, final long[] array2, final long[] array3) {
        for (int i = 0; i < 9; ++i) {
            final int n = i;
            array3[n] ^= (array[i] ^ array2[i]);
        }
    }
    
    private static void addBothTo(final long[] array, final int n, final long[] array2, final int n2, final long[] array3, final int n3) {
        for (int i = 0; i < 9; ++i) {
            final int n4 = n3 + i;
            array3[n4] ^= (array[n + i] ^ array2[n2 + i]);
        }
    }
    
    public static void addExt(final long[] array, final long[] array2, final long[] array3) {
        for (int i = 0; i < 18; ++i) {
            array3[i] = (array[i] ^ array2[i]);
        }
    }
    
    public static void addOne(final long[] array, final long[] array2) {
        array2[0] = (array[0] ^ 0x1L);
        for (int i = 1; i < 9; ++i) {
            array2[i] = array[i];
        }
    }
    
    public static long[] fromBigInteger(final BigInteger bigInteger) {
        final long[] fromBigInteger64 = Nat576.fromBigInteger64(bigInteger);
        reduce5(fromBigInteger64, 0);
        return fromBigInteger64;
    }
    
    public static void invert(final long[] array, final long[] array2) {
        if (Nat576.isZero64(array)) {
            throw new IllegalStateException();
        }
        final long[] create64 = Nat576.create64();
        final long[] create65 = Nat576.create64();
        final long[] create66 = Nat576.create64();
        square(array, create66);
        square(create66, create64);
        square(create64, create65);
        multiply(create64, create65, create64);
        squareN(create64, 2, create65);
        multiply(create64, create65, create64);
        multiply(create64, create66, create64);
        squareN(create64, 5, create65);
        multiply(create64, create65, create64);
        squareN(create65, 5, create65);
        multiply(create64, create65, create64);
        squareN(create64, 15, create65);
        multiply(create64, create65, create66);
        squareN(create66, 30, create64);
        squareN(create64, 30, create65);
        multiply(create64, create65, create64);
        squareN(create64, 60, create65);
        multiply(create64, create65, create64);
        squareN(create65, 60, create65);
        multiply(create64, create65, create64);
        squareN(create64, 180, create65);
        multiply(create64, create65, create64);
        squareN(create65, 180, create65);
        multiply(create64, create65, create64);
        multiply(create64, create66, array2);
    }
    
    public static void multiply(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat576.createExt64();
        implMultiply(array, array2, ext64);
        reduce(ext64, array3);
    }
    
    public static void multiplyAddToExt(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat576.createExt64();
        implMultiply(array, array2, ext64);
        addExt(array3, ext64, array3);
    }
    
    public static void multiplyPrecomp(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat576.createExt64();
        implMultiplyPrecomp(array, array2, ext64);
        reduce(ext64, array3);
    }
    
    public static void multiplyPrecompAddToExt(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat576.createExt64();
        implMultiplyPrecomp(array, array2, ext64);
        addExt(array3, ext64, array3);
    }
    
    public static long[] precompMultiplicand(final long[] array) {
        final int n = 144;
        final long[] array2 = new long[n << 1];
        System.arraycopy(array, 0, array2, 9, 9);
        int n2 = 0;
        for (int i = 7; i > 0; --i) {
            n2 += 18;
            Nat.shiftUpBit64(9, array2, n2 >>> 1, 0L, array2, n2);
            reduce5(array2, n2);
            add(array2, 9, array2, n2, array2, n2 + 9);
        }
        Nat.shiftUpBits64(n, array2, 0, 4, 0L, array2, n);
        return array2;
    }
    
    public static void reduce(final long[] array, final long[] array2) {
        final long n = array[9];
        final long n2 = array[17];
        final long n3 = n ^ n2 >>> 59 ^ n2 >>> 57 ^ n2 >>> 54 ^ n2 >>> 49;
        long n4 = array[8] ^ n2 << 5 ^ n2 << 7 ^ n2 << 10 ^ n2 << 15;
        for (int i = 16; i >= 10; --i) {
            final long n5 = array[i];
            array2[i - 8] = (n4 ^ n5 >>> 59 ^ n5 >>> 57 ^ n5 >>> 54 ^ n5 >>> 49);
            n4 = (array[i - 9] ^ n5 << 5 ^ n5 << 7 ^ n5 << 10 ^ n5 << 15);
        }
        final long n6 = n3;
        array2[1] = (n4 ^ n6 >>> 59 ^ n6 >>> 57 ^ n6 >>> 54 ^ n6 >>> 49);
        final long n7 = array[0] ^ n6 << 5 ^ n6 << 7 ^ n6 << 10 ^ n6 << 15;
        final long n8 = array2[8];
        final long n9 = n8 >>> 59;
        array2[0] = (n7 ^ n9 ^ n9 << 2 ^ n9 << 5 ^ n9 << 10);
        array2[8] = (n8 & 0x7FFFFFFFFFFFFFFL);
    }
    
    public static void reduce5(final long[] array, final int n) {
        final long n2 = array[n + 8];
        final long n3 = n2 >>> 59;
        array[n] ^= (n3 ^ n3 << 2 ^ n3 << 5 ^ n3 << 10);
        array[n + 8] = (n2 & 0x7FFFFFFFFFFFFFFL);
    }
    
    public static void sqrt(final long[] array, final long[] array2) {
        final long[] create64 = Nat576.create64();
        final long[] create65 = Nat576.create64();
        int n = 0;
        for (int i = 0; i < 4; ++i) {
            final long unshuffle = Interleave.unshuffle(array[n++]);
            final long unshuffle2 = Interleave.unshuffle(array[n++]);
            create64[i] = ((unshuffle & 0xFFFFFFFFL) | unshuffle2 << 32);
            create65[i] = (unshuffle >>> 32 | (unshuffle2 & 0xFFFFFFFF00000000L));
        }
        final long unshuffle3 = Interleave.unshuffle(array[n]);
        create64[4] = (unshuffle3 & 0xFFFFFFFFL);
        create65[4] = unshuffle3 >>> 32;
        multiply(create65, SecT571Field.ROOT_Z, array2);
        add(array2, create64, array2);
    }
    
    public static void square(final long[] array, final long[] array2) {
        final long[] ext64 = Nat576.createExt64();
        implSquare(array, ext64);
        reduce(ext64, array2);
    }
    
    public static void squareAddToExt(final long[] array, final long[] array2) {
        final long[] ext64 = Nat576.createExt64();
        implSquare(array, ext64);
        addExt(array2, ext64, array2);
    }
    
    public static void squareN(final long[] array, int n, final long[] array2) {
        final long[] ext64 = Nat576.createExt64();
        implSquare(array, ext64);
        reduce(ext64, array2);
        while (--n > 0) {
            implSquare(array2, ext64);
            reduce(ext64, array2);
        }
    }
    
    public static int trace(final long[] array) {
        return (int)(array[0] ^ array[8] >>> 49 ^ array[8] >>> 57) & 0x1;
    }
    
    protected static void implMultiply(final long[] array, final long[] array2, final long[] array3) {
        implMultiplyPrecomp(array, precompMultiplicand(array2), array3);
    }
    
    protected static void implMultiplyPrecomp(final long[] array, final long[] array2, final long[] array3) {
        final int n = 15;
        for (int i = 56; i >= 0; i -= 8) {
            for (int j = 1; j < 9; j += 2) {
                final int n2 = (int)(array[j] >>> i);
                addBothTo(array2, 9 * (n2 & n), array2, 9 * ((n2 >>> 4 & n) + 16), array3, j - 1);
            }
            Nat.shiftUpBits64(16, array3, 0, 8, 0L);
        }
        for (int k = 56; k >= 0; k -= 8) {
            for (int l = 0; l < 9; l += 2) {
                final int n3 = (int)(array[l] >>> k);
                addBothTo(array2, 9 * (n3 & n), array2, 9 * ((n3 >>> 4 & n) + 16), array3, l);
            }
            if (k > 0) {
                Nat.shiftUpBits64(18, array3, 0, 8, 0L);
            }
        }
    }
    
    protected static void implMulwAcc(final long[] array, final long n, final long[] array2, final int n2) {
        final long[] array3 = new long[32];
        array3[1] = n;
        for (int i = 2; i < 32; i += 2) {
            array3[i] = array3[i >>> 1] << 1;
            array3[i + 1] = (array3[i] ^ n);
        }
        long n3 = 0L;
        for (int j = 0; j < 9; ++j) {
            long n4 = array[j];
            long n5 = n3 ^ array3[(int)n4 & 0x1F];
            long n6 = 0L;
            int k = 60;
            do {
                final long n7 = array3[(int)(n4 >>> k) & 0x1F];
                n5 ^= n7 << k;
                n6 ^= n7 >>> -k;
                k -= 5;
            } while (k > 0);
            for (int l = 0; l < 4; ++l) {
                n4 = (n4 & 0xEF7BDEF7BDEF7BDEL) >>> 1;
                n6 ^= (n4 & n << l >> 63);
            }
            final int n8 = n2 + j;
            array2[n8] ^= n5;
            n3 = n6;
        }
        final int n9 = n2 + 9;
        array2[n9] ^= n3;
    }
    
    protected static void implSquare(final long[] array, final long[] array2) {
        for (int i = 0; i < 9; ++i) {
            Interleave.expand64To128(array[i], array2, i << 1);
        }
    }
    
    static {
        ROOT_Z = new long[] { 3161836309350906777L, -7642453882179322845L, -3821226941089661423L, 7312758566309945096L, -556661012383879292L, 8945041530681231562L, -4750851271514160027L, 6847946401097695794L, 541669439031730457L };
    }
}
