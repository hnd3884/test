package org.bouncycastle.crypto.ec;

import org.bouncycastle.crypto.CipherParameters;

public interface ECPairTransform
{
    void init(final CipherParameters p0);
    
    ECPair transform(final ECPair p0);
}
