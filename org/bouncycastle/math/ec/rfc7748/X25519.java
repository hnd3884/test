package org.bouncycastle.math.ec.rfc7748;

public abstract class X25519
{
    private static final int C_A = 486662;
    private static final int C_A24 = 121666;
    private static final int[] PsubS_x;
    private static int[] precompBase;
    
    private static int decode32(final byte[] array, int n) {
        return (array[n] & 0xFF) | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF) << 16 | array[++n] << 24;
    }
    
    private static void decodeScalar(final byte[] array, final int n, final int[] array2) {
        for (int i = 0; i < 8; ++i) {
            array2[i] = decode32(array, n + i * 4);
        }
        final int n2 = 0;
        array2[n2] &= 0xFFFFFFF8;
        final int n3 = 7;
        array2[n3] &= Integer.MAX_VALUE;
        final int n4 = 7;
        array2[n4] |= 0x40000000;
    }
    
    private static void pointDouble(final int[] array, final int[] array2) {
        final int[] create = X25519Field.create();
        final int[] create2 = X25519Field.create();
        X25519Field.apm(array, array2, create, create2);
        X25519Field.sqr(create, create);
        X25519Field.sqr(create2, create2);
        X25519Field.mul(create, create2, array);
        X25519Field.sub(create, create2, create);
        X25519Field.mul(create, 121666, array2);
        X25519Field.add(array2, create2, array2);
        X25519Field.mul(array2, create, array2);
    }
    
    public static synchronized void precompute() {
        if (X25519.precompBase != null) {
            return;
        }
        X25519.precompBase = new int[2520];
        final int[] precompBase = X25519.precompBase;
        final int[] array = new int[2510];
        final int[] create = X25519Field.create();
        create[0] = 9;
        final int[] create2 = X25519Field.create();
        create2[0] = 1;
        final int[] create3 = X25519Field.create();
        final int[] create4 = X25519Field.create();
        X25519Field.apm(create, create2, create3, create4);
        final int[] create5 = X25519Field.create();
        X25519Field.copy(create4, 0, create5, 0);
        int n = 0;
        while (true) {
            X25519Field.copy(create3, 0, precompBase, n);
            if (n == 2510) {
                break;
            }
            pointDouble(create, create2);
            X25519Field.apm(create, create2, create3, create4);
            X25519Field.mul(create3, create5, create3);
            X25519Field.mul(create5, create4, create5);
            X25519Field.copy(create4, 0, array, n);
            n += 10;
        }
        final int[] create6 = X25519Field.create();
        X25519Field.inv(create5, create6);
        while (true) {
            X25519Field.copy(precompBase, n, create, 0);
            X25519Field.mul(create, create6, create);
            X25519Field.copy(create, 0, X25519.precompBase, n);
            if (n == 0) {
                break;
            }
            n -= 10;
            X25519Field.copy(array, n, create2, 0);
            X25519Field.mul(create6, create2, create6);
        }
    }
    
    public static void scalarMult(final byte[] array, final int n, final byte[] array2, final int n2, final byte[] array3, final int n3) {
        final int[] array4 = new int[8];
        decodeScalar(array, n, array4);
        final int[] create = X25519Field.create();
        X25519Field.decode(array2, n2, create);
        final int[] create2 = X25519Field.create();
        X25519Field.copy(create, 0, create2, 0);
        final int[] create3 = X25519Field.create();
        create3[0] = 1;
        final int[] create4 = X25519Field.create();
        create4[0] = 1;
        final int[] create5 = X25519Field.create();
        final int[] create6 = X25519Field.create();
        final int[] create7 = X25519Field.create();
        int i = 254;
        int n4 = 1;
        do {
            X25519Field.apm(create4, create5, create6, create4);
            X25519Field.apm(create2, create3, create5, create2);
            X25519Field.mul(create6, create2, create6);
            X25519Field.mul(create4, create5, create4);
            X25519Field.sqr(create5, create5);
            X25519Field.sqr(create2, create2);
            X25519Field.sub(create5, create2, create7);
            X25519Field.mul(create7, 121666, create3);
            X25519Field.add(create3, create2, create3);
            X25519Field.mul(create3, create7, create3);
            X25519Field.mul(create2, create5, create2);
            X25519Field.apm(create6, create4, create4, create5);
            X25519Field.sqr(create4, create4);
            X25519Field.sqr(create5, create5);
            X25519Field.mul(create5, create, create5);
            final int n5 = array4[--i >>> 5] >>> (i & 0x1F) & 0x1;
            final int n6 = n4 ^ n5;
            X25519Field.cswap(n6, create2, create4);
            X25519Field.cswap(n6, create3, create5);
            n4 = n5;
        } while (i >= 3);
        for (int j = 0; j < 3; ++j) {
            pointDouble(create2, create3);
        }
        X25519Field.inv(create3, create3);
        X25519Field.mul(create2, create3, create2);
        X25519Field.normalize(create2);
        X25519Field.encode(create2, array3, n3);
    }
    
    public static void scalarMultBase(final byte[] array, final int n, final byte[] array2, final int n2) {
        precompute();
        final int[] array3 = new int[8];
        decodeScalar(array, n, array3);
        final int[] create = X25519Field.create();
        final int[] create2 = X25519Field.create();
        create2[0] = 1;
        final int[] create3 = X25519Field.create();
        create3[0] = 1;
        final int[] create4 = X25519Field.create();
        X25519Field.copy(X25519.PsubS_x, 0, create4, 0);
        final int[] create5 = X25519Field.create();
        create5[0] = 1;
        final int[] array4 = create2;
        final int[] array5 = create3;
        final int[] array6 = create;
        final int[] array7 = array4;
        final int[] array8 = array5;
        int n3 = 0;
        int n4 = 3;
        int n5 = 1;
        do {
            X25519Field.copy(X25519.precompBase, n3, create, 0);
            n3 += 10;
            final int n6 = array3[n4 >>> 5] >>> (n4 & 0x1F) & 0x1;
            final int n7 = n5 ^ n6;
            X25519Field.cswap(n7, create2, create4);
            X25519Field.cswap(n7, create3, create5);
            n5 = n6;
            X25519Field.apm(create2, create3, array4, array5);
            X25519Field.mul(create, array5, array6);
            X25519Field.carry(array4);
            X25519Field.apm(array4, array6, array7, array8);
            X25519Field.sqr(array7, array7);
            X25519Field.sqr(array8, array8);
            X25519Field.mul(create5, array7, create2);
            X25519Field.mul(create4, array8, create3);
        } while (++n4 < 255);
        for (int i = 0; i < 3; ++i) {
            pointDouble(create2, create3);
        }
        X25519Field.inv(create3, create3);
        X25519Field.mul(create2, create3, create2);
        X25519Field.normalize(create2);
        X25519Field.encode(create2, array2, n2);
    }
    
    static {
        PsubS_x = new int[] { 64258704, 46628941, 18905110, 42949224, 8920788, 10663709, 35115447, 21804323, 8973338, 4366948 };
        X25519.precompBase = null;
    }
}
