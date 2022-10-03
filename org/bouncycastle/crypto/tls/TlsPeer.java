package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface TlsPeer
{
    boolean shouldUseGMTUnixTime();
    
    void notifySecureRenegotiation(final boolean p0) throws IOException;
    
    TlsCompression getCompression() throws IOException;
    
    TlsCipher getCipher() throws IOException;
    
    void notifyAlertRaised(final short p0, final short p1, final String p2, final Throwable p3);
    
    void notifyAlertReceived(final short p0, final short p1);
    
    void notifyHandshakeComplete() throws IOException;
}
