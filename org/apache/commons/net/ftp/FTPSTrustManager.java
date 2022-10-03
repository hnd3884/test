package org.apache.commons.net.ftp;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

@Deprecated
public class FTPSTrustManager implements X509TrustManager
{
    private static final X509Certificate[] EMPTY_X509CERTIFICATE_ARRAY;
    
    @Override
    public void checkClientTrusted(final X509Certificate[] certificates, final String authType) {
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] certificates, final String authType) throws CertificateException {
        for (final X509Certificate certificate : certificates) {
            certificate.checkValidity();
        }
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return FTPSTrustManager.EMPTY_X509CERTIFICATE_ARRAY;
    }
    
    static {
        EMPTY_X509CERTIFICATE_ARRAY = new X509Certificate[0];
    }
}
