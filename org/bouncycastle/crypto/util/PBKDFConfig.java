package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public abstract class PBKDFConfig
{
    private final ASN1ObjectIdentifier algorithm;
    
    protected PBKDFConfig(final ASN1ObjectIdentifier algorithm) {
        this.algorithm = algorithm;
    }
    
    public ASN1ObjectIdentifier getAlgorithm() {
        return this.algorithm;
    }
}
