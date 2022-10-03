package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;

public class AESWrapEngine extends RFC3394WrapEngine
{
    public AESWrapEngine() {
        super(new AESEngine());
    }
    
    public AESWrapEngine(final boolean b) {
        super(new AESEngine(), b);
    }
}
