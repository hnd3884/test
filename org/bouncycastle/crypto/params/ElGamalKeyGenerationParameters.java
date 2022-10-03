package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class ElGamalKeyGenerationParameters extends KeyGenerationParameters
{
    private ElGamalParameters params;
    
    public ElGamalKeyGenerationParameters(final SecureRandom secureRandom, final ElGamalParameters params) {
        super(secureRandom, getStrength(params));
        this.params = params;
    }
    
    public ElGamalParameters getParameters() {
        return this.params;
    }
    
    static int getStrength(final ElGamalParameters elGamalParameters) {
        return (elGamalParameters.getL() != 0) ? elGamalParameters.getL() : elGamalParameters.getP().bitLength();
    }
}
