package org.bouncycastle.est.jcajce;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SecureRandom;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.KeyManager;
import java.security.Provider;

class SSLSocketFactoryCreatorBuilder
{
    protected String tlsVersion;
    protected Provider tlsProvider;
    protected KeyManager[] keyManagers;
    protected X509TrustManager[] trustManagers;
    protected SecureRandom secureRandom;
    
    public SSLSocketFactoryCreatorBuilder(final X509TrustManager x509TrustManager) {
        this.tlsVersion = "TLS";
        this.secureRandom = new SecureRandom();
        if (x509TrustManager == null) {
            throw new NullPointerException("Trust managers can not be null");
        }
        this.trustManagers = new X509TrustManager[] { x509TrustManager };
    }
    
    public SSLSocketFactoryCreatorBuilder(final X509TrustManager[] trustManagers) {
        this.tlsVersion = "TLS";
        this.secureRandom = new SecureRandom();
        if (trustManagers == null) {
            throw new NullPointerException("Trust managers can not be null");
        }
        this.trustManagers = trustManagers;
    }
    
    public SSLSocketFactoryCreatorBuilder withTLSVersion(final String tlsVersion) {
        this.tlsVersion = tlsVersion;
        return this;
    }
    
    public SSLSocketFactoryCreatorBuilder withSecureRandom(final SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }
    
    public SSLSocketFactoryCreatorBuilder withProvider(final String s) throws NoSuchProviderException {
        this.tlsProvider = Security.getProvider(s);
        if (this.tlsProvider == null) {
            throw new NoSuchProviderException("JSSE provider not found: " + s);
        }
        return this;
    }
    
    public SSLSocketFactoryCreatorBuilder withProvider(final Provider tlsProvider) {
        this.tlsProvider = tlsProvider;
        return this;
    }
    
    public SSLSocketFactoryCreatorBuilder withKeyManager(final KeyManager keyManager) {
        if (keyManager == null) {
            this.keyManagers = null;
        }
        else {
            this.keyManagers = new KeyManager[] { keyManager };
        }
        return this;
    }
    
    public SSLSocketFactoryCreatorBuilder withKeyManagers(final KeyManager[] keyManagers) {
        this.keyManagers = keyManagers;
        return this;
    }
    
    public SSLSocketFactoryCreator build() {
        return new SSLSocketFactoryCreator() {
            public boolean isTrusted() {
                for (int i = 0; i != SSLSocketFactoryCreatorBuilder.this.trustManagers.length; ++i) {
                    if (SSLSocketFactoryCreatorBuilder.this.trustManagers[i].getAcceptedIssuers().length > 0) {
                        return true;
                    }
                }
                return false;
            }
            
            public SSLSocketFactory createFactory() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
                SSLContext sslContext;
                if (SSLSocketFactoryCreatorBuilder.this.tlsProvider != null) {
                    sslContext = SSLContext.getInstance(SSLSocketFactoryCreatorBuilder.this.tlsVersion, SSLSocketFactoryCreatorBuilder.this.tlsProvider);
                }
                else {
                    sslContext = SSLContext.getInstance(SSLSocketFactoryCreatorBuilder.this.tlsVersion);
                }
                sslContext.init(SSLSocketFactoryCreatorBuilder.this.keyManagers, SSLSocketFactoryCreatorBuilder.this.trustManagers, SSLSocketFactoryCreatorBuilder.this.secureRandom);
                return sslContext.getSocketFactory();
            }
        };
    }
}
