package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class DSAKeyGenerationParameters extends KeyGenerationParameters
{
    private DSAParameters params;
    
    public DSAKeyGenerationParameters(final SecureRandom secureRandom, final DSAParameters params) {
        super(secureRandom, params.getP().bitLength() - 1);
        this.params = params;
    }
    
    public DSAParameters getParameters() {
        return this.params;
    }
}
