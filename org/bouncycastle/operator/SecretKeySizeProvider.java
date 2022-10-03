package org.bouncycastle.operator;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface SecretKeySizeProvider
{
    int getKeySize(final AlgorithmIdentifier p0);
    
    int getKeySize(final ASN1ObjectIdentifier p0);
}
