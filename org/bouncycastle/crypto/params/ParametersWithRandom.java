package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithRandom implements CipherParameters
{
    private SecureRandom random;
    private CipherParameters parameters;
    
    public ParametersWithRandom(final CipherParameters parameters, final SecureRandom random) {
        this.random = random;
        this.parameters = parameters;
    }
    
    public ParametersWithRandom(final CipherParameters cipherParameters) {
        this(cipherParameters, new SecureRandom());
    }
    
    public SecureRandom getRandom() {
        return this.random;
    }
    
    public CipherParameters getParameters() {
        return this.parameters;
    }
}
