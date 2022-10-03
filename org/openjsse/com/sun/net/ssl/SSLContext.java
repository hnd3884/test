package org.openjsse.com.sun.net.ssl;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

@Deprecated
public class SSLContext
{
    private Provider provider;
    private SSLContextSpi contextSpi;
    private String protocol;
    
    protected SSLContext(final SSLContextSpi contextSpi, final Provider provider, final String protocol) {
        this.contextSpi = contextSpi;
        this.provider = provider;
        this.protocol = protocol;
    }
    
    public static SSLContext getInstance(final String protocol) throws NoSuchAlgorithmException {
        try {
            final Object[] objs = SSLSecurity.getImpl(protocol, "SSLContext", (String)null);
            return new SSLContext((SSLContextSpi)objs[0], (Provider)objs[1], protocol);
        }
        catch (final NoSuchProviderException e) {
            throw new NoSuchAlgorithmException(protocol + " not found");
        }
    }
    
    public static SSLContext getInstance(final String protocol, final String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.length() == 0) {
            throw new IllegalArgumentException("missing provider");
        }
        final Object[] objs = SSLSecurity.getImpl(protocol, "SSLContext", provider);
        return new SSLContext((SSLContextSpi)objs[0], (Provider)objs[1], protocol);
    }
    
    public static SSLContext getInstance(final String protocol, final Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("missing provider");
        }
        final Object[] objs = SSLSecurity.getImpl(protocol, "SSLContext", provider);
        return new SSLContext((SSLContextSpi)objs[0], (Provider)objs[1], protocol);
    }
    
    public final String getProtocol() {
        return this.protocol;
    }
    
    public final Provider getProvider() {
        return this.provider;
    }
    
    public final void init(final KeyManager[] km, final TrustManager[] tm, final SecureRandom random) throws KeyManagementException {
        this.contextSpi.engineInit(km, tm, random);
    }
    
    public final SSLSocketFactory getSocketFactory() {
        return this.contextSpi.engineGetSocketFactory();
    }
    
    public final SSLServerSocketFactory getServerSocketFactory() {
        return this.contextSpi.engineGetServerSocketFactory();
    }
}
