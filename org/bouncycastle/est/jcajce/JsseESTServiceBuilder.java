package org.bouncycastle.est.jcajce;

import java.net.Socket;
import org.bouncycastle.est.ESTService;
import javax.net.ssl.KeyManager;
import java.security.Provider;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Arrays;
import org.bouncycastle.est.ESTClientProvider;
import javax.net.ssl.X509TrustManager;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.est.ESTServiceBuilder;

public class JsseESTServiceBuilder extends ESTServiceBuilder
{
    protected SSLSocketFactoryCreator socketFactoryCreator;
    protected JsseHostnameAuthorizer hostNameAuthorizer;
    protected int timeoutMillis;
    protected ChannelBindingProvider bindingProvider;
    protected Set<String> supportedSuites;
    protected Long absoluteLimit;
    protected SSLSocketFactoryCreatorBuilder sslSocketFactoryCreatorBuilder;
    protected boolean filterCipherSuites;
    
    public JsseESTServiceBuilder(final String s, final SSLSocketFactoryCreator socketFactoryCreator) {
        super(s);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet<String>();
        this.filterCipherSuites = true;
        if (socketFactoryCreator == null) {
            throw new NullPointerException("No socket factory creator.");
        }
        this.socketFactoryCreator = socketFactoryCreator;
    }
    
    public JsseESTServiceBuilder(final String s) {
        super(s);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet<String>();
        this.filterCipherSuites = true;
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(JcaJceUtils.getTrustAllTrustManager());
    }
    
    public JsseESTServiceBuilder(final String s, final X509TrustManager x509TrustManager) {
        super(s);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet<String>();
        this.filterCipherSuites = true;
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(x509TrustManager);
    }
    
    public JsseESTServiceBuilder(final String s, final X509TrustManager[] array) {
        super(s);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet<String>();
        this.filterCipherSuites = true;
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(array);
    }
    
    public JsseESTServiceBuilder withHostNameAuthorizer(final JsseHostnameAuthorizer hostNameAuthorizer) {
        this.hostNameAuthorizer = hostNameAuthorizer;
        return this;
    }
    
    @Override
    public JsseESTServiceBuilder withClientProvider(final ESTClientProvider clientProvider) {
        this.clientProvider = clientProvider;
        return this;
    }
    
    public JsseESTServiceBuilder withTimeout(final int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }
    
    public JsseESTServiceBuilder withReadLimit(final long n) {
        this.absoluteLimit = n;
        return this;
    }
    
    public JsseESTServiceBuilder withChannelBindingProvider(final ChannelBindingProvider bindingProvider) {
        this.bindingProvider = bindingProvider;
        return this;
    }
    
    public JsseESTServiceBuilder addCipherSuites(final String s) {
        this.supportedSuites.add(s);
        return this;
    }
    
    public JsseESTServiceBuilder addCipherSuites(final String[] array) {
        this.supportedSuites.addAll(Arrays.asList(array));
        return this;
    }
    
    public JsseESTServiceBuilder withTLSVersion(final String s) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withTLSVersion(s);
        return this;
    }
    
    public JsseESTServiceBuilder withSecureRandom(final SecureRandom secureRandom) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withSecureRandom(secureRandom);
        return this;
    }
    
    public JsseESTServiceBuilder withProvider(final String s) throws NoSuchProviderException {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withProvider(s);
        return this;
    }
    
    public JsseESTServiceBuilder withProvider(final Provider provider) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withProvider(provider);
        return this;
    }
    
    public JsseESTServiceBuilder withKeyManager(final KeyManager keyManager) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withKeyManager(keyManager);
        return this;
    }
    
    public JsseESTServiceBuilder withKeyManagers(final KeyManager[] array) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withKeyManagers(array);
        return this;
    }
    
    public JsseESTServiceBuilder withFilterCipherSuites(final boolean filterCipherSuites) {
        this.filterCipherSuites = filterCipherSuites;
        return this;
    }
    
    @Override
    public ESTService build() {
        if (this.bindingProvider == null) {
            this.bindingProvider = new ChannelBindingProvider() {
                public boolean canAccessChannelBinding(final Socket socket) {
                    return false;
                }
                
                public byte[] getChannelBinding(final Socket socket, final String s) {
                    return null;
                }
            };
        }
        if (this.socketFactoryCreator == null) {
            this.socketFactoryCreator = this.sslSocketFactoryCreatorBuilder.build();
        }
        if (this.clientProvider == null) {
            this.clientProvider = new DefaultESTHttpClientProvider(this.hostNameAuthorizer, this.socketFactoryCreator, this.timeoutMillis, this.bindingProvider, this.supportedSuites, this.absoluteLimit, this.filterCipherSuites);
        }
        return super.build();
    }
}
