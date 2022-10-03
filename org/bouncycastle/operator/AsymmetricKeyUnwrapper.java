package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public abstract class AsymmetricKeyUnwrapper implements KeyUnwrapper
{
    private AlgorithmIdentifier algorithmId;
    
    protected AsymmetricKeyUnwrapper(final AlgorithmIdentifier algorithmId) {
        this.algorithmId = algorithmId;
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmId;
    }
}
