package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class CramerShoupKeyGenerationParameters extends KeyGenerationParameters
{
    private CramerShoupParameters params;
    
    public CramerShoupKeyGenerationParameters(final SecureRandom secureRandom, final CramerShoupParameters params) {
        super(secureRandom, getStrength(params));
        this.params = params;
    }
    
    public CramerShoupParameters getParameters() {
        return this.params;
    }
    
    static int getStrength(final CramerShoupParameters cramerShoupParameters) {
        return cramerShoupParameters.getP().bitLength();
    }
}
