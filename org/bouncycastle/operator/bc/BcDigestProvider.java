package org.bouncycastle.operator.bc;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface BcDigestProvider
{
    ExtendedDigest get(final AlgorithmIdentifier p0) throws OperatorCreationException;
}
