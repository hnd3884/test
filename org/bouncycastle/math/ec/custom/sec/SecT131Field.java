package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat192;
import java.math.BigInteger;

public class SecT131Field
{
    private static final long M03 = 7L;
    private static final long M44 = 17592186044415L;
    private static final long[] ROOT_Z;
    
    public static void add(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
        array3[2] = (array[2] ^ array2[2]);
    }
    
    public static void addExt(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
        array3[2] = (array[2] ^ array2[2]);
        array3[3] = (array[3] ^ array2[3]);
        array3[4] = (array[4] ^ array2[4]);
    }
    
    public static void addOne(final long[] array, final long[] array2) {
        array2[0] = (array[0] ^ 0x1L);
        array2[1] = array[1];
        array2[2] = array[2];
    }
    
    public static long[] fromBigInteger(final BigInteger bigInteger) {
        final long[] fromBigInteger64 = Nat192.fromBigInteger64(bigInteger);
        reduce61(fromBigInteger64, 0);
        return fromBigInteger64;
    }
    
    public static void invert(final long[] array, final long[] array2) {
        if (Nat192.isZero64(array)) {
            throw new IllegalStateException();
        }
        final long[] create64 = Nat192.create64();
        final long[] create65 = Nat192.create64();
        square(array, create64);
        multiply(create64, array, create64);
        squareN(create64, 2, create65);
        multiply(create65, create64, create65);
        squareN(create65, 4, create64);
        multiply(create64, create65, create64);
        squareN(create64, 8, create65);
        multiply(create65, create64, create65);
        squareN(create65, 16, create64);
        multiply(create64, create65, create64);
        squareN(create64, 32, create65);
        multiply(create65, create64, create65);
        square(create65, create65);
        multiply(create65, array, create65);
        squareN(create65, 65, create64);
        multiply(create64, create65, create64);
        square(create64, array2);
    }
    
    public static void multiply(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat192.createExt64();
        implMultiply(array, array2, ext64);
        reduce(ext64, array3);
    }
    
    public static void multiplyAddToExt(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat192.createExt64();
        implMultiply(array, array2, ext64);
        addExt(array3, ext64, array3);
    }
    
    public static void reduce(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = array[4];
        final long n6 = n2 ^ (n5 << 61 ^ n5 << 63);
        final long n7 = n3 ^ (n5 >>> 3 ^ n5 >>> 1 ^ n5 ^ n5 << 5);
        final long n8 = n4 ^ n5 >>> 59;
        final long n9 = n ^ (n8 << 61 ^ n8 << 63);
        final long n10 = n6 ^ (n8 >>> 3 ^ n8 >>> 1 ^ n8 ^ n8 << 5);
        final long n11 = n7 ^ n8 >>> 59;
        final long n12 = n11 >>> 3;
        array2[0] = (n9 ^ n12 ^ n12 << 2 ^ n12 << 3 ^ n12 << 8);
        array2[1] = (n10 ^ n12 >>> 56);
        array2[2] = (n11 & 0x7L);
    }
    
    public static void reduce61(final long[] array, final int n) {
        final long n2 = array[n + 2];
        final long n3 = n2 >>> 3;
        array[n] ^= (n3 ^ n3 << 2 ^ n3 << 3 ^ n3 << 8);
        final int n4 = n + 1;
        array[n4] ^= n3 >>> 56;
        array[n + 2] = (n2 & 0x7L);
    }
    
    public static void sqrt(final long[] array, final long[] array2) {
        final long[] create64 = Nat192.create64();
        final long unshuffle = Interleave.unshuffle(array[0]);
        final long unshuffle2 = Interleave.unshuffle(array[1]);
        final long n = (unshuffle & 0xFFFFFFFFL) | unshuffle2 << 32;
        create64[0] = (unshuffle >>> 32 | (unshuffle2 & 0xFFFFFFFF00000000L));
        final long unshuffle3 = Interleave.unshuffle(array[2]);
        final long n2 = unshuffle3 & 0xFFFFFFFFL;
        create64[1] = unshuffle3 >>> 32;
        multiply(create64, SecT131Field.ROOT_Z, array2);
        final int n3 = 0;
        array2[n3] ^= n;
        final int n4 = 1;
        array2[n4] ^= n2;
    }
    
    public static void square(final long[] array, final long[] array2) {
        final long[] create64 = Nat.create64(5);
        implSquare(array, create64);
        reduce(create64, array2);
    }
    
    public static void squareAddToExt(final long[] array, final long[] array2) {
        final long[] create64 = Nat.create64(5);
        implSquare(array, create64);
        addExt(array2, create64, array2);
    }
    
    public static void squareN(final long[] array, int n, final long[] array2) {
        final long[] create64 = Nat.create64(5);
        implSquare(array, create64);
        reduce(create64, array2);
        while (--n > 0) {
            implSquare(array2, create64);
            reduce(create64, array2);
        }
    }
    
    public static int trace(final long[] array) {
        return (int)(array[0] ^ array[1] >>> 59 ^ array[2] >>> 1) & 0x1;
    }
    
    protected static void implCompactExt(final long[] array) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = array[4];
        final long n6 = array[5];
        array[0] = (n ^ n2 << 44);
        array[1] = (n2 >>> 20 ^ n3 << 24);
        array[2] = (n3 >>> 40 ^ n4 << 4 ^ n5 << 48);
        array[3] = (n4 >>> 60 ^ n6 << 28 ^ n5 >>> 16);
        array[4] = n6 >>> 36;
        array[5] = 0L;
    }
    
    protected static void implMultiply(final long[] array, final long[] array2, final long[] array3) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = (n2 >>> 24 ^ array[2] << 40) & 0xFFFFFFFFFFFL;
        final long n4 = (n >>> 44 ^ n2 << 20) & 0xFFFFFFFFFFFL;
        final long n5 = n & 0xFFFFFFFFFFFL;
        final long n6 = array2[0];
        final long n7 = array2[1];
        final long n8 = (n7 >>> 24 ^ array2[2] << 40) & 0xFFFFFFFFFFFL;
        final long n9 = (n6 >>> 44 ^ n7 << 20) & 0xFFFFFFFFFFFL;
        final long n10 = n6 & 0xFFFFFFFFFFFL;
        final long[] array4 = new long[10];
        implMulw(n5, n10, array4, 0);
        implMulw(n3, n8, array4, 2);
        final long n11 = n5 ^ n4 ^ n3;
        final long n12 = n10 ^ n9 ^ n8;
        implMulw(n11, n12, array4, 4);
        final long n13 = n4 << 1 ^ n3 << 2;
        final long n14 = n9 << 1 ^ n8 << 2;
        implMulw(n5 ^ n13, n10 ^ n14, array4, 6);
        implMulw(n11 ^ n13, n12 ^ n14, array4, 8);
        final long n15 = array4[6] ^ array4[8];
        final long n16 = array4[7] ^ array4[9];
        final long n17 = n15 << 1 ^ array4[6];
        final long n18 = n15 ^ n16 << 1 ^ array4[7];
        final long n19 = n16;
        final long n20 = array4[0];
        final long n21 = array4[1] ^ array4[0] ^ array4[4];
        final long n22 = array4[1] ^ array4[5];
        final long n23 = n20 ^ n17 ^ array4[2] << 4 ^ array4[2] << 1;
        final long n24 = n21 ^ n18 ^ array4[3] << 4 ^ array4[3] << 1;
        final long n25 = n22 ^ n19;
        final long n26 = n24 ^ n23 >>> 44;
        final long n27 = n23 & 0xFFFFFFFFFFFL;
        final long n28 = n25 ^ n26 >>> 44;
        final long n29 = n26 & 0xFFFFFFFFFFFL;
        final long n30 = n27 >>> 1 ^ (n29 & 0x1L) << 43;
        final long n31 = n29 >>> 1 ^ (n28 & 0x1L) << 43;
        final long n32 = n28 >>> 1;
        final long n33 = n30 ^ n30 << 1;
        final long n34 = n33 ^ n33 << 2;
        final long n35 = n34 ^ n34 << 4;
        final long n36 = n35 ^ n35 << 8;
        final long n37 = n36 ^ n36 << 16;
        final long n38 = (n37 ^ n37 << 32) & 0xFFFFFFFFFFFL;
        final long n39 = n31 ^ n38 >>> 43;
        final long n40 = n39 ^ n39 << 1;
        final long n41 = n40 ^ n40 << 2;
        final long n42 = n41 ^ n41 << 4;
        final long n43 = n42 ^ n42 << 8;
        final long n44 = n43 ^ n43 << 16;
        final long n45 = (n44 ^ n44 << 32) & 0xFFFFFFFFFFFL;
        final long n46 = n32 ^ n45 >>> 43;
        final long n47 = n46 ^ n46 << 1;
        final long n48 = n47 ^ n47 << 2;
        final long n49 = n48 ^ n48 << 4;
        final long n50 = n49 ^ n49 << 8;
        final long n51 = n50 ^ n50 << 16;
        final long n52 = n51 ^ n51 << 32;
        array3[0] = n20;
        array3[1] = (n21 ^ n38 ^ array4[2]);
        array3[2] = (n22 ^ n45 ^ n38 ^ array4[3]);
        array3[3] = (n52 ^ n45);
        array3[4] = (n52 ^ array4[2]);
        array3[5] = array4[3];
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
        long n6 = array2[n4 & 0x7] ^ array2[n4 >>> 3 & 0x7] << 3 ^ array2[n4 >>> 6 & 0x7] << 6;
        int i = 33;
        do {
            final int n7 = (int)(n >>> i);
            final long n8 = array2[n7 & 0x7] ^ array2[n7 >>> 3 & 0x7] << 3 ^ array2[n7 >>> 6 & 0x7] << 6 ^ array2[n7 >>> 9 & 0x7] << 9;
            n6 ^= n8 << i;
            n5 ^= n8 >>> -i;
            i -= 12;
        } while (i > 0);
        array[n3] = (n6 & 0xFFFFFFFFFFFL);
        array[n3 + 1] = (n6 >>> 44 ^ n5 << 20);
    }
    
    protected static void implSquare(final long[] array, final long[] array2) {
        Interleave.expand64To128(array[0], array2, 0);
        Interleave.expand64To128(array[1], array2, 2);
        array2[4] = ((long)Interleave.expand8to16((int)array[2]) & 0xFFFFFFFFL);
    }
    
    static {
        ROOT_Z = new long[] { 2791191049453778211L, 2791191049453778402L, 6L };
    }
}
