package org.bouncycastle.crypto.paddings;

import org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;

public interface BlockCipherPadding
{
    void init(final SecureRandom p0) throws IllegalArgumentException;
    
    String getPaddingName();
    
    int addPadding(final byte[] p0, final int p1);
    
    int padCount(final byte[] p0) throws InvalidCipherTextException;
}
