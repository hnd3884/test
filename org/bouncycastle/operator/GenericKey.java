package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class GenericKey
{
    private AlgorithmIdentifier algorithmIdentifier;
    private Object representation;
    
    @Deprecated
    public GenericKey(final Object representation) {
        this.algorithmIdentifier = null;
        this.representation = representation;
    }
    
    public GenericKey(final AlgorithmIdentifier algorithmIdentifier, final byte[] representation) {
        this.algorithmIdentifier = algorithmIdentifier;
        this.representation = representation;
    }
    
    protected GenericKey(final AlgorithmIdentifier algorithmIdentifier, final Object representation) {
        this.algorithmIdentifier = algorithmIdentifier;
        this.representation = representation;
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmIdentifier;
    }
    
    public Object getRepresentation() {
        return this.representation;
    }
}
