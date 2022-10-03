package org.bouncycastle.crypto.ec;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.CipherParameters;

public interface ECEncryptor
{
    void init(final CipherParameters p0);
    
    ECPair encrypt(final ECPoint p0);
}
