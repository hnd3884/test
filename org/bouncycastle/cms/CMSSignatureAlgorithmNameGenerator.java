package org.bouncycastle.cms;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface CMSSignatureAlgorithmNameGenerator
{
    String getSignatureName(final AlgorithmIdentifier p0, final AlgorithmIdentifier p1);
}
