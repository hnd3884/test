package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.Pack;

public class SCrypt
{
    public static byte[] generate(final byte[] array, final byte[] array2, final int n, final int n2, final int n3, final int n4) {
        if (array == null) {
            throw new IllegalArgumentException("Passphrase P must be provided.");
        }
        if (array2 == null) {
            throw new IllegalArgumentException("Salt S must be provided.");
        }
        if (n <= 1 || !isPowerOf2(n)) {
            throw new IllegalArgumentException("Cost parameter N must be > 1 and a power of 2");
        }
        if (n2 == 1 && n >= 65536) {
            throw new IllegalArgumentException("Cost parameter N must be > 1 and < 65536.");
        }
        if (n2 < 1) {
            throw new IllegalArgumentException("Block size r must be >= 1.");
        }
        final int n5 = Integer.MAX_VALUE / (128 * n2 * 8);
        if (n3 < 1 || n3 > n5) {
            throw new IllegalArgumentException("Parallelisation parameter p must be >= 1 and <= " + n5 + " (based on block size r of " + n2 + ")");
        }
        if (n4 < 1) {
            throw new IllegalArgumentException("Generated key length dkLen must be >= 1.");
        }
        return MFcrypt(array, array2, n, n2, n3, n4);
    }
    
    private static byte[] MFcrypt(final byte[] array, final byte[] array2, final int n, final int n2, final int n3, final int n4) {
        final int n5 = n2 * 128;
        final byte[] singleIterationPBKDF2 = SingleIterationPBKDF2(array, array2, n3 * n5);
        int[] array3 = null;
        try {
            final int n6 = singleIterationPBKDF2.length >>> 2;
            array3 = new int[n6];
            Pack.littleEndianToInt(singleIterationPBKDF2, 0, array3);
            for (int n7 = n5 >>> 2, i = 0; i < n6; i += n7) {
                SMix(array3, i, n, n2);
            }
            Pack.intToLittleEndian(array3, singleIterationPBKDF2, 0);
            return SingleIterationPBKDF2(array, singleIterationPBKDF2, n4);
        }
        finally {
            Clear(singleIterationPBKDF2);
            Clear(array3);
        }
    }
    
    private static byte[] SingleIterationPBKDF2(final byte[] array, final byte[] array2, final int n) {
        final PKCS5S2ParametersGenerator pkcs5S2ParametersGenerator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        pkcs5S2ParametersGenerator.init(array, array2, 1);
        return ((KeyParameter)pkcs5S2ParametersGenerator.generateDerivedMacParameters(n * 8)).getKey();
    }
    
    private static void SMix(final int[] array, final int n, final int n2, final int n3) {
        final int n4 = n3 * 32;
        final int[] array2 = new int[16];
        final int[] array3 = new int[16];
        final int[] array4 = new int[n4];
        final int[] array5 = new int[n4];
        final int[][] array6 = new int[n2][];
        try {
            System.arraycopy(array, n, array5, 0, n4);
            for (int i = 0; i < n2; ++i) {
                array6[i] = Arrays.clone(array5);
                BlockMix(array5, array2, array3, array4, n3);
            }
            final int n5 = n2 - 1;
            for (int j = 0; j < n2; ++j) {
                Xor(array5, array6[array5[n4 - 16] & n5], 0, array5);
                BlockMix(array5, array2, array3, array4, n3);
            }
            System.arraycopy(array5, 0, array, n, n4);
        }
        finally {
            ClearAll(array6);
            ClearAll(new int[][] { array5, array2, array3, array4 });
        }
    }
    
    private static void BlockMix(final int[] array, final int[] array2, final int[] array3, final int[] array4, final int n) {
        System.arraycopy(array, array.length - 16, array2, 0, 16);
        int n2 = 0;
        int n3 = 0;
        final int n4 = array.length >>> 1;
        for (int i = 2 * n; i > 0; --i) {
            Xor(array2, array, n2, array3);
            Salsa20Engine.salsaCore(8, array3, array2);
            System.arraycopy(array2, 0, array4, n3, 16);
            n3 = n4 + n2 - n3;
            n2 += 16;
        }
        System.arraycopy(array4, 0, array, 0, array4.length);
    }
    
    private static void Xor(final int[] array, final int[] array2, final int n, final int[] array3) {
        for (int i = array3.length - 1; i >= 0; --i) {
            array3[i] = (array[i] ^ array2[n + i]);
        }
    }
    
    private static void Clear(final byte[] array) {
        if (array != null) {
            Arrays.fill(array, (byte)0);
        }
    }
    
    private static void Clear(final int[] array) {
        if (array != null) {
            Arrays.fill(array, 0);
        }
    }
    
    private static void ClearAll(final int[][] array) {
        for (int i = 0; i < array.length; ++i) {
            Clear(array[i]);
        }
    }
    
    private static boolean isPowerOf2(final int n) {
        return (n & n - 1) == 0x0;
    }
}
