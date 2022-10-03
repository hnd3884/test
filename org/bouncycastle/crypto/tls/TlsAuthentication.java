package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface TlsAuthentication
{
    void notifyServerCertificate(final Certificate p0) throws IOException;
    
    TlsCredentials getClientCredentials(final CertificateRequest p0) throws IOException;
}
