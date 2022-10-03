package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface InputDecryptorProvider
{
    InputDecryptor get(final AlgorithmIdentifier p0) throws OperatorCreationException;
}
