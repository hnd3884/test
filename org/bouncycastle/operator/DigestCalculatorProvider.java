package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface DigestCalculatorProvider
{
    DigestCalculator get(final AlgorithmIdentifier p0) throws OperatorCreationException;
}
