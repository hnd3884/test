package org.apache.tomcat.util.net;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.IOException;

public class AprSSLSupport implements SSLSupport
{
    private final AprEndpoint.AprSocketWrapper socketWrapper;
    private final String clientCertProvider;
    
    public AprSSLSupport(final AprEndpoint.AprSocketWrapper socketWrapper, final String clientCertProvider) {
        this.socketWrapper = socketWrapper;
        this.clientCertProvider = clientCertProvider;
    }
    
    @Override
    public String getCipherSuite() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoS(2);
        }
        catch (final Exception e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public X509Certificate[] getPeerCertificateChain() throws IOException {
        try {
            int certLength = this.socketWrapper.getSSLInfoI(1024);
            final byte[] clientCert = this.socketWrapper.getSSLInfoB(263);
            X509Certificate[] certs = null;
            if (clientCert != null) {
                if (certLength < 0) {
                    certLength = 0;
                }
                certs = new X509Certificate[certLength + 1];
                CertificateFactory cf;
                if (this.clientCertProvider == null) {
                    cf = CertificateFactory.getInstance("X.509");
                }
                else {
                    cf = CertificateFactory.getInstance("X.509", this.clientCertProvider);
                }
                certs[0] = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(clientCert));
                for (int i = 0; i < certLength; ++i) {
                    final byte[] data = this.socketWrapper.getSSLInfoB(1024 + i);
                    certs[i + 1] = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(data));
                }
            }
            return certs;
        }
        catch (final Exception e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public Integer getKeySize() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoI(3);
        }
        catch (final Exception e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public String getSessionId() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoS(1);
        }
        catch (final Exception e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public String getProtocol() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoS(7);
        }
        catch (final Exception e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public String getRequestedProtocols() throws IOException {
        return null;
    }
    
    @Override
    public String getRequestedCiphers() throws IOException {
        return null;
    }
}
