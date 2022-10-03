package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class DHKeyGenerationParameters extends KeyGenerationParameters
{
    private DHParameters params;
    
    public DHKeyGenerationParameters(final SecureRandom secureRandom, final DHParameters params) {
        super(secureRandom, getStrength(params));
        this.params = params;
    }
    
    public DHParameters getParameters() {
        return this.params;
    }
    
    static int getStrength(final DHParameters dhParameters) {
        return (dhParameters.getL() != 0) ? dhParameters.getL() : dhParameters.getP().bitLength();
    }
}
