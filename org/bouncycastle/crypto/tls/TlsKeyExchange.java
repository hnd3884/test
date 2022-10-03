package org.bouncycastle.crypto.tls;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public interface TlsKeyExchange
{
    void init(final TlsContext p0);
    
    void skipServerCredentials() throws IOException;
    
    void processServerCredentials(final TlsCredentials p0) throws IOException;
    
    void processServerCertificate(final Certificate p0) throws IOException;
    
    boolean requiresServerKeyExchange();
    
    byte[] generateServerKeyExchange() throws IOException;
    
    void skipServerKeyExchange() throws IOException;
    
    void processServerKeyExchange(final InputStream p0) throws IOException;
    
    void validateCertificateRequest(final CertificateRequest p0) throws IOException;
    
    void skipClientCredentials() throws IOException;
    
    void processClientCredentials(final TlsCredentials p0) throws IOException;
    
    void processClientCertificate(final Certificate p0) throws IOException;
    
    void generateClientKeyExchange(final OutputStream p0) throws IOException;
    
    void processClientKeyExchange(final InputStream p0) throws IOException;
    
    byte[] generatePremasterSecret() throws IOException;
}
