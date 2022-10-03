package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.MacCalculator;

public interface PKCS12MacCalculatorBuilder
{
    MacCalculator build(final char[] p0) throws OperatorCreationException;
    
    AlgorithmIdentifier getDigestAlgorithmIdentifier();
}
