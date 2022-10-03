package org.bouncycastle.crypto.paddings;

import org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;

public class ISO10126d2Padding implements BlockCipherPadding
{
    SecureRandom random;
    
    public void init(final SecureRandom random) throws IllegalArgumentException {
        if (random != null) {
            this.random = random;
        }
        else {
            this.random = new SecureRandom();
        }
    }
    
    public String getPaddingName() {
        return "ISO10126-2";
    }
    
    public int addPadding(final byte[] array, int i) {
        final byte b = (byte)(array.length - i);
        while (i < array.length - 1) {
            array[i] = (byte)this.random.nextInt();
            ++i;
        }
        return array[i] = b;
    }
    
    public int padCount(final byte[] array) throws InvalidCipherTextException {
        final int n = array[array.length - 1] & 0xFF;
        if (n > array.length) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return n;
    }
}
