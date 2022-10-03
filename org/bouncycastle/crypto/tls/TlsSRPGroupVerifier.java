package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.params.SRP6GroupParameters;

public interface TlsSRPGroupVerifier
{
    boolean accept(final SRP6GroupParameters p0);
}
