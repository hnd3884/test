package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class ECKeyGenerationParameters extends KeyGenerationParameters
{
    private ECDomainParameters domainParams;
    
    public ECKeyGenerationParameters(final ECDomainParameters domainParams, final SecureRandom secureRandom) {
        super(secureRandom, domainParams.getN().bitLength());
        this.domainParams = domainParams;
    }
    
    public ECDomainParameters getDomainParameters() {
        return this.domainParams;
    }
}
