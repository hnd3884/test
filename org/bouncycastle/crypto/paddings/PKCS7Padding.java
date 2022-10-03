package org.bouncycastle.crypto.paddings;

import org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;

public class PKCS7Padding implements BlockCipherPadding
{
    public void init(final SecureRandom secureRandom) throws IllegalArgumentException {
    }
    
    public String getPaddingName() {
        return "PKCS7";
    }
    
    public int addPadding(final byte[] array, int i) {
        final byte b = (byte)(array.length - i);
        while (i < array.length) {
            array[i] = b;
            ++i;
        }
        return b;
    }
    
    public int padCount(final byte[] array) throws InvalidCipherTextException {
        final int n = array[array.length - 1] & 0xFF;
        final byte b = (byte)n;
        boolean b2 = n > array.length | n == 0;
        for (int i = 0; i < array.length; ++i) {
            b2 |= (array.length - i <= n & array[i] != b);
        }
        if (b2) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return n;
    }
}
