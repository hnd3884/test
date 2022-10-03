package org.bouncycastle.crypto.tls;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.RandomGenerator;

public interface TlsContext
{
    RandomGenerator getNonceRandomGenerator();
    
    SecureRandom getSecureRandom();
    
    SecurityParameters getSecurityParameters();
    
    boolean isServer();
    
    ProtocolVersion getClientVersion();
    
    ProtocolVersion getServerVersion();
    
    TlsSession getResumableSession();
    
    Object getUserObject();
    
    void setUserObject(final Object p0);
    
    byte[] exportKeyingMaterial(final String p0, final byte[] p1, final int p2);
}
