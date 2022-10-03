package org.bouncycastle.crypto.tls;

import java.security.SecureRandom;

class TlsServerContextImpl extends AbstractTlsContext implements TlsServerContext
{
    TlsServerContextImpl(final SecureRandom secureRandom, final SecurityParameters securityParameters) {
        super(secureRandom, securityParameters);
    }
    
    public boolean isServer() {
        return true;
    }
}
