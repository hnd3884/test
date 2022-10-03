package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;

public class ARIAWrapEngine extends RFC3394WrapEngine
{
    public ARIAWrapEngine() {
        super(new ARIAEngine());
    }
    
    public ARIAWrapEngine(final boolean b) {
        super(new ARIAEngine(), b);
    }
}
