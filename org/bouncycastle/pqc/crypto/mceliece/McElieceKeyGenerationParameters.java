package org.bouncycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class McElieceKeyGenerationParameters extends KeyGenerationParameters
{
    private McElieceParameters params;
    
    public McElieceKeyGenerationParameters(final SecureRandom secureRandom, final McElieceParameters params) {
        super(secureRandom, 256);
        this.params = params;
    }
    
    public McElieceParameters getParameters() {
        return this.params;
    }
}
