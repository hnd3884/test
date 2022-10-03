package org.bouncycastle.crypto.paddings;

import org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;

public class TBCPadding implements BlockCipherPadding
{
    public void init(final SecureRandom secureRandom) throws IllegalArgumentException {
    }
    
    public String getPaddingName() {
        return "TBC";
    }
    
    public int addPadding(final byte[] array, int i) {
        final int n = array.length - i;
        byte b;
        if (i > 0) {
            b = (byte)(((array[i - 1] & 0x1) == 0x0) ? 255 : 0);
        }
        else {
            b = (byte)(((array[array.length - 1] & 0x1) == 0x0) ? 255 : 0);
        }
        while (i < array.length) {
            array[i] = b;
            ++i;
        }
        return n;
    }
    
    public int padCount(final byte[] array) throws InvalidCipherTextException {
        byte b;
        int n;
        for (b = array[array.length - 1], n = array.length - 1; n > 0 && array[n - 1] == b; --n) {}
        return array.length - n;
    }
}
