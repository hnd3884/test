package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public abstract class SymmetricKeyUnwrapper implements KeyUnwrapper
{
    private AlgorithmIdentifier algorithmId;
    
    protected SymmetricKeyUnwrapper(final AlgorithmIdentifier algorithmId) {
        this.algorithmId = algorithmId;
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmId;
    }
}
