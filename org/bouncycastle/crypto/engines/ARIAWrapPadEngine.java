package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;

public class ARIAWrapPadEngine extends RFC5649WrapEngine
{
    public ARIAWrapPadEngine() {
        super(new ARIAEngine());
    }
}
