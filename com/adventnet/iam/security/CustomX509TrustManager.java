package com.adventnet.iam.security;

import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import javax.net.ssl.X509TrustManager;

public class CustomX509TrustManager implements X509TrustManager
{
    private static final Logger LOGGER;
    private X509TrustManager jvmTrustManager;
    private X509TrustManager customTrustManager;
    public X509Certificate[] chain;
    
    public CustomX509TrustManager(final X509TrustManager defaultMgr, final X509TrustManager customMgr) throws Exception {
        this.jvmTrustManager = defaultMgr;
        this.customTrustManager = customMgr;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        try {
            this.jvmTrustManager.checkClientTrusted(chain, authType);
        }
        catch (final CertificateException ex) {
            CustomX509TrustManager.LOGGER.log(Level.FINE, "JVM Trust Manager threw Exception", ex.getMessage());
            this.customTrustManager.checkClientTrusted(chain, authType);
        }
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        try {
            this.chain = chain;
            this.jvmTrustManager.checkServerTrusted(chain, authType);
        }
        catch (final CertificateException cx) {
            CustomX509TrustManager.LOGGER.log(Level.FINE, "JVM Trust Manager threw Exception", cx.getMessage());
            this.customTrustManager.checkServerTrusted(chain, authType);
        }
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        final X509Certificate[] issuers = this.jvmTrustManager.getAcceptedIssuers();
        final X509Certificate[] pvtIssuers = this.customTrustManager.getAcceptedIssuers();
        final X509Certificate[] allIssuers = new X509Certificate[issuers.length + pvtIssuers.length];
        for (int i = 0; i < issuers.length; ++i) {
            allIssuers[i] = issuers[i];
        }
        for (int i = issuers.length, j = 0; i < allIssuers.length; ++i, ++j) {
            allIssuers[i] = pvtIssuers[j];
        }
        return allIssuers;
    }
    
    static {
        LOGGER = Logger.getLogger(CustomX509TrustManager.class.getName());
    }
}
