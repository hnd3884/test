package org.bouncycastle.cert.crmf;

import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface ValueDecryptorGenerator
{
    InputDecryptor getValueDecryptor(final AlgorithmIdentifier p0, final AlgorithmIdentifier p1, final byte[] p2) throws CRMFException;
}
