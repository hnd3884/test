package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Digest;

public interface TlsHandshakeHash extends Digest
{
    void init(final TlsContext p0);
    
    TlsHandshakeHash notifyPRFDetermined();
    
    void trackHashAlgorithm(final short p0);
    
    void sealHashAlgorithms();
    
    TlsHandshakeHash stopTracking();
    
    Digest forkPRFHash();
    
    byte[] getFinalHash(final short p0);
}
