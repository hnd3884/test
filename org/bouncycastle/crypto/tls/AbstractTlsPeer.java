package org.bouncycastle.crypto.tls;

import java.io.IOException;

public abstract class AbstractTlsPeer implements TlsPeer
{
    public boolean shouldUseGMTUnixTime() {
        return false;
    }
    
    public void notifySecureRenegotiation(final boolean b) throws IOException {
        if (!b) {
            throw new TlsFatalAlert((short)40);
        }
    }
    
    public void notifyAlertRaised(final short n, final short n2, final String s, final Throwable t) {
    }
    
    public void notifyAlertReceived(final short n, final short n2) {
    }
    
    public void notifyHandshakeComplete() throws IOException {
    }
}
