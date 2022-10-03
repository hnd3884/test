package org.bouncycastle.pqc.crypto.gmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class GMSSKeyGenerationParameters extends KeyGenerationParameters
{
    private GMSSParameters params;
    
    public GMSSKeyGenerationParameters(final SecureRandom secureRandom, final GMSSParameters params) {
        super(secureRandom, 1);
        this.params = params;
    }
    
    public GMSSParameters getParameters() {
        return this.params;
    }
}
