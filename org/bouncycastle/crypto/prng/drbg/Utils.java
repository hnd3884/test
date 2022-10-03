package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.util.Integers;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Digest;
import java.util.Hashtable;

class Utils
{
    static final Hashtable maxSecurityStrengths;
    
    static int getMaxSecurityStrength(final Digest digest) {
        return Utils.maxSecurityStrengths.get(digest.getAlgorithmName());
    }
    
    static int getMaxSecurityStrength(final Mac mac) {
        final String algorithmName = mac.getAlgorithmName();
        return (int)Utils.maxSecurityStrengths.get(algorithmName.substring(0, algorithmName.indexOf("/")));
    }
    
    static byte[] hash_df(final Digest digest, final byte[] array, final int n) {
        final byte[] array2 = new byte[(n + 7) / 8];
        final int n2 = array2.length / digest.getDigestSize();
        int n3 = 1;
        final byte[] array3 = new byte[digest.getDigestSize()];
        for (int i = 0; i <= n2; ++i) {
            digest.update((byte)n3);
            digest.update((byte)(n >> 24));
            digest.update((byte)(n >> 16));
            digest.update((byte)(n >> 8));
            digest.update((byte)n);
            digest.update(array, 0, array.length);
            digest.doFinal(array3, 0);
            System.arraycopy(array3, 0, array2, i * array3.length, (array2.length - i * array3.length > array3.length) ? array3.length : (array2.length - i * array3.length));
            ++n3;
        }
        if (n % 8 != 0) {
            final int n4 = 8 - n % 8;
            int n5 = 0;
            for (int j = 0; j != array2.length; ++j) {
                final int n6 = array2[j] & 0xFF;
                array2[j] = (byte)(n6 >>> n4 | n5 << 8 - n4);
                n5 = n6;
            }
        }
        return array2;
    }
    
    static boolean isTooLarge(final byte[] array, final int n) {
        return array != null && array.length > n;
    }
    
    static {
        (maxSecurityStrengths = new Hashtable()).put("SHA-1", Integers.valueOf(128));
        Utils.maxSecurityStrengths.put("SHA-224", Integers.valueOf(192));
        Utils.maxSecurityStrengths.put("SHA-256", Integers.valueOf(256));
        Utils.maxSecurityStrengths.put("SHA-384", Integers.valueOf(256));
        Utils.maxSecurityStrengths.put("SHA-512", Integers.valueOf(256));
        Utils.maxSecurityStrengths.put("SHA-512/224", Integers.valueOf(192));
        Utils.maxSecurityStrengths.put("SHA-512/256", Integers.valueOf(256));
    }
}
