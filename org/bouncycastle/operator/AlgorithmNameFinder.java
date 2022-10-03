package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface AlgorithmNameFinder
{
    boolean hasAlgorithmName(final ASN1ObjectIdentifier p0);
    
    String getAlgorithmName(final ASN1ObjectIdentifier p0);
    
    String getAlgorithmName(final AlgorithmIdentifier p0);
}
