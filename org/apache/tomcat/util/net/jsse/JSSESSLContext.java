package org.apache.tomcat.util.net.jsse;

import java.util.Set;
import java.util.Collection;
import java.util.Arrays;
import javax.net.ssl.X509TrustManager;
import java.util.HashSet;
import javax.net.ssl.X509KeyManager;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import org.apache.tomcat.util.net.SSLContext;

class JSSESSLContext implements SSLContext
{
    private javax.net.ssl.SSLContext context;
    private KeyManager[] kms;
    private TrustManager[] tms;
    
    JSSESSLContext(final String protocol) throws NoSuchAlgorithmException {
        this.context = javax.net.ssl.SSLContext.getInstance(protocol);
    }
    
    @Override
    public void init(final KeyManager[] kms, final TrustManager[] tms, final SecureRandom sr) throws KeyManagementException {
        this.kms = kms;
        this.tms = tms;
        this.context.init(kms, tms, sr);
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public SSLSessionContext getServerSessionContext() {
        return this.context.getServerSessionContext();
    }
    
    @Override
    public SSLEngine createSSLEngine() {
        return this.context.createSSLEngine();
    }
    
    @Override
    public SSLServerSocketFactory getServerSocketFactory() {
        return this.context.getServerSocketFactory();
    }
    
    @Override
    public SSLParameters getSupportedSSLParameters() {
        return this.context.getSupportedSSLParameters();
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        X509Certificate[] result = null;
        if (this.kms != null) {
            for (int i = 0; i < this.kms.length && result == null; ++i) {
                if (this.kms[i] instanceof X509KeyManager) {
                    result = ((X509KeyManager)this.kms[i]).getCertificateChain(alias);
                }
            }
        }
        return result;
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        final Set<X509Certificate> certs = new HashSet<X509Certificate>();
        if (this.tms != null) {
            for (final TrustManager tm : this.tms) {
                if (tm instanceof X509TrustManager) {
                    final X509Certificate[] accepted = ((X509TrustManager)tm).getAcceptedIssuers();
                    if (accepted != null) {
                        certs.addAll(Arrays.asList(accepted));
                    }
                }
            }
        }
        return certs.toArray(new X509Certificate[0]);
    }
}
