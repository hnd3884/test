package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public final class XMSSKeyGenerationParameters extends KeyGenerationParameters
{
    private final XMSSParameters xmssParameters;
    
    public XMSSKeyGenerationParameters(final XMSSParameters xmssParameters, final SecureRandom secureRandom) {
        super(secureRandom, -1);
        this.xmssParameters = xmssParameters;
    }
    
    public XMSSParameters getParameters() {
        return this.xmssParameters;
    }
}
