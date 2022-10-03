package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface KeyUnwrapper
{
    AlgorithmIdentifier getAlgorithmIdentifier();
    
    GenericKey generateUnwrappedKey(final AlgorithmIdentifier p0, final byte[] p1) throws OperatorException;
}
