package org.bouncycastle.crypto.tls;

public interface TlsSRPIdentityManager
{
    TlsSRPLoginParameters getLoginParameters(final byte[] p0);
}
