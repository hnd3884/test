package org.bouncycastle.crypto.paddings;

import org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;

public class ZeroBytePadding implements BlockCipherPadding
{
    public void init(final SecureRandom secureRandom) throws IllegalArgumentException {
    }
    
    public String getPaddingName() {
        return "ZeroByte";
    }
    
    public int addPadding(final byte[] array, int i) {
        final int n = array.length - i;
        while (i < array.length) {
            array[i] = 0;
            ++i;
        }
        return n;
    }
    
    public int padCount(final byte[] array) throws InvalidCipherTextException {
        int length;
        for (length = array.length; length > 0 && array[length - 1] == 0; --length) {}
        return array.length - length;
    }
}
