package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat320;
import java.math.BigInteger;

public class SecT283Field
{
    private static final long M27 = 134217727L;
    private static final long M57 = 144115188075855871L;
    private static final long[] ROOT_Z;
    
    public static void add(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
        array3[2] = (array[2] ^ array2[2]);
        array3[3] = (array[3] ^ array2[3]);
        array3[4] = (array[4] ^ array2[4]);
    }
    
    public static void addExt(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
        array3[2] = (array[2] ^ array2[2]);
        array3[3] = (array[3] ^ array2[3]);
        array3[4] = (array[4] ^ array2[4]);
        array3[5] = (array[5] ^ array2[5]);
        array3[6] = (array[6] ^ array2[6]);
        array3[7] = (array[7] ^ array2[7]);
        array3[8] = (array[8] ^ array2[8]);
    }
    
    public static void addOne(final long[] array, final long[] array2) {
        array2[0] = (array[0] ^ 0x1L);
        array2[1] = array[1];
        array2[2] = array[2];
        array2[3] = array[3];
        array2[4] = array[4];
    }
    
    public static long[] fromBigInteger(final BigInteger bigInteger) {
        final long[] fromBigInteger64 = Nat320.fromBigInteger64(bigInteger);
        reduce37(fromBigInteger64, 0);
        return fromBigInteger64;
    }
    
    public static void invert(final long[] array, final long[] array2) {
        if (Nat320.isZero64(array)) {
            throw new IllegalStateException();
        }
        final long[] create64 = Nat320.create64();
        final long[] create65 = Nat320.create64();
        square(array, create64);
        multiply(create64, array, create64);
        squareN(create64, 2, create65);
        multiply(create65, create64, create65);
        squareN(create65, 4, create64);
        multiply(create64, create65, create64);
        squareN(create64, 8, create65);
        multiply(create65, create64, create65);
        square(create65, create65);
        multiply(create65, array, create65);
        squareN(create65, 17, create64);
        multiply(create64, create65, create64);
        square(create64, create64);
        multiply(create64, array, create64);
        squareN(create64, 35, create65);
        multiply(create65, create64, create65);
        squareN(create65, 70, create64);
        multiply(create64, create65, create64);
        square(create64, create64);
        multiply(create64, array, create64);
        squareN(create64, 141, create65);
        multiply(create65, create64, create65);
        square(create65, array2);
    }
    
    public static void multiply(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat320.createExt64();
        implMultiply(array, array2, ext64);
        reduce(ext64, array3);
    }
    
    public static void multiplyAddToExt(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat320.createExt64();
        implMultiply(array, array2, ext64);
        addExt(array3, ext64, array3);
    }
    
    public static void reduce(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = array[4];
        final long n6 = array[5];
        final long n7 = array[6];
        final long n8 = array[7];
        final long n9 = array[8];
        final long n10 = n4 ^ (n9 << 37 ^ n9 << 42 ^ n9 << 44 ^ n9 << 49);
        final long n11 = n5 ^ (n9 >>> 27 ^ n9 >>> 22 ^ n9 >>> 20 ^ n9 >>> 15);
        final long n12 = n3 ^ (n8 << 37 ^ n8 << 42 ^ n8 << 44 ^ n8 << 49);
        final long n13 = n10 ^ (n8 >>> 27 ^ n8 >>> 22 ^ n8 >>> 20 ^ n8 >>> 15);
        final long n14 = n2 ^ (n7 << 37 ^ n7 << 42 ^ n7 << 44 ^ n7 << 49);
        final long n15 = n12 ^ (n7 >>> 27 ^ n7 >>> 22 ^ n7 >>> 20 ^ n7 >>> 15);
        final long n16 = n ^ (n6 << 37 ^ n6 << 42 ^ n6 << 44 ^ n6 << 49);
        final long n17 = n14 ^ (n6 >>> 27 ^ n6 >>> 22 ^ n6 >>> 20 ^ n6 >>> 15);
        final long n18 = n11 >>> 27;
        array2[0] = (n16 ^ n18 ^ n18 << 5 ^ n18 << 7 ^ n18 << 12);
        array2[1] = n17;
        array2[2] = n15;
        array2[3] = n13;
        array2[4] = (n11 & 0x7FFFFFFL);
    }
    
    public static void reduce37(final long[] array, final int n) {
        final long n2 = array[n + 4];
        final long n3 = n2 >>> 27;
        array[n] ^= (n3 ^ n3 << 5 ^ n3 << 7 ^ n3 << 12);
        array[n + 4] = (n2 & 0x7FFFFFFL);
    }
    
    public static void sqrt(final long[] array, final long[] array2) {
        final long[] create64 = Nat320.create64();
        final long unshuffle = Interleave.unshuffle(array[0]);
        final long unshuffle2 = Interleave.unshuffle(array[1]);
        final long n = (unshuffle & 0xFFFFFFFFL) | unshuffle2 << 32;
        create64[0] = (unshuffle >>> 32 | (unshuffle2 & 0xFFFFFFFF00000000L));
        final long unshuffle3 = Interleave.unshuffle(array[2]);
        final long unshuffle4 = Interleave.unshuffle(array[3]);
        final long n2 = (unshuffle3 & 0xFFFFFFFFL) | unshuffle4 << 32;
        create64[1] = (unshuffle3 >>> 32 | (unshuffle4 & 0xFFFFFFFF00000000L));
        final long unshuffle5 = Interleave.unshuffle(array[4]);
        final long n3 = unshuffle5 & 0xFFFFFFFFL;
        create64[2] = unshuffle5 >>> 32;
        multiply(create64, SecT283Field.ROOT_Z, array2);
        final int n4 = 0;
        array2[n4] ^= n;
        final int n5 = 1;
        array2[n5] ^= n2;
        final int n6 = 2;
        array2[n6] ^= n3;
    }
    
    public static void square(final long[] array, final long[] array2) {
        final long[] create64 = Nat.create64(9);
        implSquare(array, create64);
        reduce(create64, array2);
    }
    
    public static void squareAddToExt(final long[] array, final long[] array2) {
        final long[] create64 = Nat.create64(9);
        implSquare(array, create64);
        addExt(array2, create64, array2);
    }
    
    public static void squareN(final long[] array, int n, final long[] array2) {
        final long[] create64 = Nat.create64(9);
        implSquare(array, create64);
        reduce(create64, array2);
        while (--n > 0) {
            implSquare(array2, create64);
            reduce(create64, array2);
        }
    }
    
    public static int trace(final long[] array) {
        return (int)(array[0] ^ array[4] >>> 15) & 0x1;
    }
    
    protected static void implCompactExt(final long[] array) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = array[4];
        final long n6 = array[5];
        final long n7 = array[6];
        final long n8 = array[7];
        final long n9 = array[8];
        final long n10 = array[9];
        array[0] = (n ^ n2 << 57);
        array[1] = (n2 >>> 7 ^ n3 << 50);
        array[2] = (n3 >>> 14 ^ n4 << 43);
        array[3] = (n4 >>> 21 ^ n5 << 36);
        array[4] = (n5 >>> 28 ^ n6 << 29);
        array[5] = (n6 >>> 35 ^ n7 << 22);
        array[6] = (n7 >>> 42 ^ n8 << 15);
        array[7] = (n8 >>> 49 ^ n9 << 8);
        array[8] = (n9 >>> 56 ^ n10 << 1);
        array[9] = n10 >>> 63;
    }
    
    protected static void implExpand(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = array[4];
        array2[0] = (n & 0x1FFFFFFFFFFFFFFL);
        array2[1] = ((n >>> 57 ^ n2 << 7) & 0x1FFFFFFFFFFFFFFL);
        array2[2] = ((n2 >>> 50 ^ n3 << 14) & 0x1FFFFFFFFFFFFFFL);
        array2[3] = ((n3 >>> 43 ^ n4 << 21) & 0x1FFFFFFFFFFFFFFL);
        array2[4] = (n4 >>> 36 ^ n5 << 28);
    }
    
    protected static void implMultiply(final long[] array, final long[] array2, final long[] array3) {
        final long[] array4 = new long[5];
        final long[] array5 = new long[5];
        implExpand(array, array4);
        implExpand(array2, array5);
        final long[] array6 = new long[26];
        implMulw(array4[0], array5[0], array6, 0);
        implMulw(array4[1], array5[1], array6, 2);
        implMulw(array4[2], array5[2], array6, 4);
        implMulw(array4[3], array5[3], array6, 6);
        implMulw(array4[4], array5[4], array6, 8);
        final long n = array4[0] ^ array4[1];
        final long n2 = array5[0] ^ array5[1];
        final long n3 = array4[0] ^ array4[2];
        final long n4 = array5[0] ^ array5[2];
        final long n5 = array4[2] ^ array4[4];
        final long n6 = array5[2] ^ array5[4];
        final long n7 = array4[3] ^ array4[4];
        final long n8 = array5[3] ^ array5[4];
        implMulw(n3 ^ array4[3], n4 ^ array5[3], array6, 18);
        implMulw(n5 ^ array4[1], n6 ^ array5[1], array6, 20);
        final long n9 = n ^ n7;
        final long n10 = n2 ^ n8;
        final long n11 = n9 ^ array4[2];
        final long n12 = n10 ^ array5[2];
        implMulw(n9, n10, array6, 22);
        implMulw(n11, n12, array6, 24);
        implMulw(n, n2, array6, 10);
        implMulw(n3, n4, array6, 12);
        implMulw(n5, n6, array6, 14);
        implMulw(n7, n8, array6, 16);
        array3[0] = array6[0];
        array3[9] = array6[9];
        final long n13 = array6[0] ^ array6[1];
        final long n14 = n13 ^ array6[2];
        final long n15 = n14 ^ array6[10];
        array3[1] = n15;
        final long n16 = array6[3] ^ array6[4];
        final long n17 = n14 ^ (n16 ^ (array6[11] ^ array6[12]));
        array3[2] = n17;
        final long n18 = n13 ^ n16;
        final long n19 = array6[5] ^ array6[6];
        final long n20 = n18 ^ n19 ^ array6[8];
        final long n21 = array6[13] ^ array6[14];
        array3[3] = (n20 ^ n21 ^ (array6[18] ^ array6[22] ^ array6[24]));
        final long n22 = array6[7] ^ array6[8] ^ array6[9];
        final long n23 = n22 ^ array6[17];
        array3[8] = n23;
        final long n24 = n22 ^ n19 ^ (array6[15] ^ array6[16]);
        array3[7] = n24;
        final long n25 = n24 ^ n15;
        final long n26 = array6[19] ^ array6[20];
        final long n27 = array6[25] ^ array6[24];
        final long n28 = array6[18] ^ array6[23];
        final long n29 = n26 ^ n27;
        array3[4] = (n29 ^ n28 ^ n25);
        array3[5] = (n29 ^ (n17 ^ n23) ^ (array6[21] ^ array6[22]));
        array3[6] = (n20 ^ array6[0] ^ array6[9] ^ n21 ^ array6[21] ^ array6[23] ^ array6[25]);
        implCompactExt(array3);
    }
    
    protected static void implMulw(final long n, final long n2, final long[] array, final int n3) {
        final long[] array2 = new long[8];
        array2[1] = n2;
        array2[2] = array2[1] << 1;
        array2[3] = (array2[2] ^ n2);
        array2[4] = array2[2] << 1;
        array2[5] = (array2[4] ^ n2);
        array2[6] = array2[3] << 1;
        array2[7] = (array2[6] ^ n2);
        final int n4 = (int)n;
        long n5 = 0L;
        long n6 = array2[n4 & 0x7];
        int i = 48;
        do {
            final int n7 = (int)(n >>> i);
            final long n8 = array2[n7 & 0x7] ^ array2[n7 >>> 3 & 0x7] << 3 ^ array2[n7 >>> 6 & 0x7] << 6;
            n6 ^= n8 << i;
            n5 ^= n8 >>> -i;
            i -= 9;
        } while (i > 0);
        final long n9 = n5 ^ (n & 0x100804020100800L & n2 << 7 >> 63) >>> 8;
        array[n3] = (n6 & 0x1FFFFFFFFFFFFFFL);
        array[n3 + 1] = (n6 >>> 57 ^ n9 << 7);
    }
    
    protected static void implSquare(final long[] array, final long[] array2) {
        for (int i = 0; i < 4; ++i) {
            Interleave.expand64To128(array[i], array2, i << 1);
        }
        array2[8] = Interleave.expand32to64((int)array[4]);
    }
    
    static {
        ROOT_Z = new long[] { 878416384462358536L, 3513665537849438403L, -9076969306111048948L, 585610922974906400L, 34087042L };
    }
}
