package org.bouncycastle.crypto.params;

public class DESedeParameters extends DESParameters
{
    public static final int DES_EDE_KEY_LENGTH = 24;
    
    public DESedeParameters(final byte[] array) {
        super(array);
        if (isWeakKey(array, 0, array.length)) {
            throw new IllegalArgumentException("attempt to create weak DESede key");
        }
    }
    
    public static boolean isWeakKey(final byte[] array, final int n, final int n2) {
        for (int i = n; i < n2; i += 8) {
            if (DESParameters.isWeakKey(array, i)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isWeakKey(final byte[] array, final int n) {
        return isWeakKey(array, n, array.length - n);
    }
    
    public static boolean isRealEDEKey(final byte[] array, final int n) {
        return (array.length == 16) ? isReal2Key(array, n) : isReal3Key(array, n);
    }
    
    public static boolean isReal2Key(final byte[] array, final int n) {
        boolean b = false;
        for (int i = n; i != n + 8; ++i) {
            if (array[i] != array[i + 8]) {
                b = true;
            }
        }
        return b;
    }
    
    public static boolean isReal3Key(final byte[] array, final int n) {
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        for (int i = n; i != n + 8; ++i) {
            b |= (array[i] != array[i + 8]);
            b2 |= (array[i] != array[i + 16]);
            b3 |= (array[i + 8] != array[i + 16]);
        }
        return b && b2 && b3;
    }
}
