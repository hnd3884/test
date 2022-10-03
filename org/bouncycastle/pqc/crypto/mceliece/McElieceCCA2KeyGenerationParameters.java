package org.bouncycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class McElieceCCA2KeyGenerationParameters extends KeyGenerationParameters
{
    private McElieceCCA2Parameters params;
    
    public McElieceCCA2KeyGenerationParameters(final SecureRandom secureRandom, final McElieceCCA2Parameters params) {
        super(secureRandom, 128);
        this.params = params;
    }
    
    public McElieceCCA2Parameters getParameters() {
        return this.params;
    }
}
