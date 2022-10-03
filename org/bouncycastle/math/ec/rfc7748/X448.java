package org.bouncycastle.math.ec.rfc7748;

public abstract class X448
{
    private static final int C_A = 156326;
    private static final int C_A24 = 39082;
    private static final int[] S_x;
    private static final int[] PsubS_x;
    private static int[] precompBase;
    
    private static int decode32(final byte[] array, int n) {
        return (array[n] & 0xFF) | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF) << 16 | array[++n] << 24;
    }
    
    private static void decodeScalar(final byte[] array, final int n, final int[] array2) {
        for (int i = 0; i < 14; ++i) {
            array2[i] = decode32(array, n + i * 4);
        }
        final int n2 = 0;
        array2[n2] &= 0xFFFFFFFC;
        final int n3 = 13;
        array2[n3] |= Integer.MIN_VALUE;
    }
    
    private static void pointDouble(final int[] array, final int[] array2) {
        final int[] create = X448Field.create();
        final int[] create2 = X448Field.create();
        X448Field.add(array, array2, create);
        X448Field.sub(array, array2, create2);
        X448Field.sqr(create, create);
        X448Field.sqr(create2, create2);
        X448Field.mul(create, create2, array);
        X448Field.sub(create, create2, create);
        X448Field.mul(create, 39082, array2);
        X448Field.add(array2, create2, array2);
        X448Field.mul(array2, create, array2);
    }
    
    public static synchronized void precompute() {
        if (X448.precompBase != null) {
            return;
        }
        X448.precompBase = new int[7136];
        final int[] precompBase = X448.precompBase;
        final int[] array = new int[7120];
        final int[] create = X448Field.create();
        create[0] = 5;
        final int[] create2 = X448Field.create();
        create2[0] = 1;
        final int[] create3 = X448Field.create();
        final int[] create4 = X448Field.create();
        X448Field.add(create, create2, create3);
        X448Field.sub(create, create2, create4);
        final int[] create5 = X448Field.create();
        X448Field.copy(create4, 0, create5, 0);
        int n = 0;
        while (true) {
            X448Field.copy(create3, 0, precompBase, n);
            if (n == 7120) {
                break;
            }
            pointDouble(create, create2);
            X448Field.add(create, create2, create3);
            X448Field.sub(create, create2, create4);
            X448Field.mul(create3, create5, create3);
            X448Field.mul(create5, create4, create5);
            X448Field.copy(create4, 0, array, n);
            n += 16;
        }
        final int[] create6 = X448Field.create();
        X448Field.inv(create5, create6);
        while (true) {
            X448Field.copy(precompBase, n, create, 0);
            X448Field.mul(create, create6, create);
            X448Field.copy(create, 0, X448.precompBase, n);
            if (n == 0) {
                break;
            }
            n -= 16;
            X448Field.copy(array, n, create2, 0);
            X448Field.mul(create6, create2, create6);
        }
    }
    
    public static void scalarMult(final byte[] array, final int n, final byte[] array2, final int n2, final byte[] array3, final int n3) {
        final int[] array4 = new int[14];
        decodeScalar(array, n, array4);
        final int[] create = X448Field.create();
        X448Field.decode(array2, n2, create);
        final int[] create2 = X448Field.create();
        X448Field.copy(create, 0, create2, 0);
        final int[] create3 = X448Field.create();
        create3[0] = 1;
        final int[] create4 = X448Field.create();
        create4[0] = 1;
        final int[] create5 = X448Field.create();
        final int[] create6 = X448Field.create();
        final int[] create7 = X448Field.create();
        int i = 447;
        int n4 = 1;
        do {
            X448Field.add(create4, create5, create6);
            X448Field.sub(create4, create5, create4);
            X448Field.add(create2, create3, create5);
            X448Field.sub(create2, create3, create2);
            X448Field.mul(create6, create2, create6);
            X448Field.mul(create4, create5, create4);
            X448Field.sqr(create5, create5);
            X448Field.sqr(create2, create2);
            X448Field.sub(create5, create2, create7);
            X448Field.mul(create7, 39082, create3);
            X448Field.add(create3, create2, create3);
            X448Field.mul(create3, create7, create3);
            X448Field.mul(create2, create5, create2);
            X448Field.sub(create6, create4, create5);
            X448Field.add(create6, create4, create4);
            X448Field.sqr(create4, create4);
            X448Field.sqr(create5, create5);
            X448Field.mul(create5, create, create5);
            final int n5 = array4[--i >>> 5] >>> (i & 0x1F) & 0x1;
            final int n6 = n4 ^ n5;
            X448Field.cswap(n6, create2, create4);
            X448Field.cswap(n6, create3, create5);
            n4 = n5;
        } while (i >= 2);
        for (int j = 0; j < 2; ++j) {
            pointDouble(create2, create3);
        }
        X448Field.inv(create3, create3);
        X448Field.mul(create2, create3, create2);
        X448Field.normalize(create2);
        X448Field.encode(create2, array3, n3);
    }
    
    public static void scalarMultBase(final byte[] array, final int n, final byte[] array2, final int n2) {
        precompute();
        final int[] array3 = new int[14];
        decodeScalar(array, n, array3);
        final int[] create = X448Field.create();
        final int[] create2 = X448Field.create();
        X448Field.copy(X448.S_x, 0, create2, 0);
        final int[] create3 = X448Field.create();
        create3[0] = 1;
        final int[] create4 = X448Field.create();
        X448Field.copy(X448.PsubS_x, 0, create4, 0);
        final int[] create5 = X448Field.create();
        create5[0] = 1;
        final int[] create6 = X448Field.create();
        final int[] array4 = create3;
        final int[] array5 = create;
        final int[] array6 = create2;
        final int[] array7 = array4;
        int n3 = 0;
        int n4 = 2;
        int n5 = 1;
        do {
            X448Field.copy(X448.precompBase, n3, create, 0);
            n3 += 16;
            final int n6 = array3[n4 >>> 5] >>> (n4 & 0x1F) & 0x1;
            final int n7 = n5 ^ n6;
            X448Field.cswap(n7, create2, create4);
            X448Field.cswap(n7, create3, create5);
            n5 = n6;
            X448Field.add(create2, create3, create6);
            X448Field.sub(create2, create3, array4);
            X448Field.mul(create, array4, array5);
            X448Field.carry(create6);
            X448Field.add(create6, array5, array6);
            X448Field.sub(create6, array5, array7);
            X448Field.sqr(array6, array6);
            X448Field.sqr(array7, array7);
            X448Field.mul(create5, array6, create2);
            X448Field.mul(create4, array7, create3);
        } while (++n4 < 448);
        for (int i = 0; i < 2; ++i) {
            pointDouble(create2, create3);
        }
        X448Field.inv(create3, create3);
        X448Field.mul(create2, create3, create2);
        X448Field.normalize(create2);
        X448Field.encode(create2, array2, n2);
    }
    
    static {
        S_x = new int[] { 268435454, 268435455, 268435455, 268435455, 268435455, 268435455, 268435455, 268435455, 268435454, 268435455, 268435455, 268435455, 268435455, 268435455, 268435455, 268435455 };
        PsubS_x = new int[] { 161294112, 185702364, 163248300, 54522310, 189866924, 105098465, 66174309, 139206530, 156517789, 136025714, 231801628, 246922668, 59251455, 69446896, 83964484, 252685170 };
        X448.precompBase = null;
    }
}
