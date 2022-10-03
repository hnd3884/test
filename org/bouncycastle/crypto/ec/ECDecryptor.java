package org.bouncycastle.crypto.ec;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.CipherParameters;

public interface ECDecryptor
{
    void init(final CipherParameters p0);
    
    ECPoint decrypt(final ECPair p0);
}
