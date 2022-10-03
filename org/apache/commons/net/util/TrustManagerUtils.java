package org.apache.commons.net.util;

import java.security.cert.CertificateException;
import java.security.GeneralSecurityException;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public final class TrustManagerUtils
{
    private static final X509Certificate[] EMPTY_X509CERTIFICATE_ARRAY;
    private static final X509TrustManager ACCEPT_ALL;
    private static final X509TrustManager CHECK_SERVER_VALIDITY;
    
    public static X509TrustManager getAcceptAllTrustManager() {
        return TrustManagerUtils.ACCEPT_ALL;
    }
    
    public static X509TrustManager getValidateServerCertificateTrustManager() {
        return TrustManagerUtils.CHECK_SERVER_VALIDITY;
    }
    
    public static X509TrustManager getDefaultTrustManager(final KeyStore keyStore) throws GeneralSecurityException {
        final String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        final TrustManagerFactory instance = TrustManagerFactory.getInstance(defaultAlgorithm);
        instance.init(keyStore);
        return (X509TrustManager)instance.getTrustManagers()[0];
    }
    
    static {
        EMPTY_X509CERTIFICATE_ARRAY = new X509Certificate[0];
        ACCEPT_ALL = new TrustManager(false);
        CHECK_SERVER_VALIDITY = new TrustManager(true);
    }
    
    private static class TrustManager implements X509TrustManager
    {
        private final boolean checkServerValidity;
        
        TrustManager(final boolean checkServerValidity) {
            this.checkServerValidity = checkServerValidity;
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] certificates, final String authType) {
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] certificates, final String authType) throws CertificateException {
            if (this.checkServerValidity) {
                for (final X509Certificate certificate : certificates) {
                    certificate.checkValidity();
                }
            }
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return TrustManagerUtils.EMPTY_X509CERTIFICATE_ARRAY;
        }
    }
}
