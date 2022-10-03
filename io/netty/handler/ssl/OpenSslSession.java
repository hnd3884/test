package io.netty.handler.ssl;

import javax.net.ssl.SSLException;
import java.security.cert.Certificate;
import javax.net.ssl.SSLSession;

interface OpenSslSession extends SSLSession
{
    OpenSslSessionId sessionId();
    
    void setLocalCertificate(final Certificate[] p0);
    
    void setSessionId(final OpenSslSessionId p0);
    
    OpenSslSessionContext getSessionContext();
    
    void tryExpandApplicationBufferSize(final int p0);
    
    void handshakeFinished(final byte[] p0, final String p1, final String p2, final byte[] p3, final byte[][] p4, final long p5, final long p6) throws SSLException;
}
