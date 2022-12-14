package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public abstract class SymmetricKeyWrapper implements KeyWrapper
{
    private AlgorithmIdentifier algorithmId;
    
    protected SymmetricKeyWrapper(final AlgorithmIdentifier algorithmId) {
        this.algorithmId = algorithmId;
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmId;
    }
}
