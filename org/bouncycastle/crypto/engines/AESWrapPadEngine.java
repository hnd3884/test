package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;

public class AESWrapPadEngine extends RFC5649WrapEngine
{
    public AESWrapPadEngine() {
        super(new AESEngine());
    }
}
