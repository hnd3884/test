package com.sun.net.ssl;

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
    
    public static SSLContext getInstance(final String s) throws NoSuchAlgorithmException {
        try {
            final Object[] impl = SSLSecurity.getImpl(s, "SSLContext", (String)null);
            return new SSLContext((SSLContextSpi)impl[0], (Provider)impl[1], s);
        }
        catch (final NoSuchProviderException ex) {
            throw new NoSuchAlgorithmException(s + " not found");
        }
    }
    
    public static SSLContext getInstance(final String s, final String s2) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (s2 == null || s2.length() == 0) {
            throw new IllegalArgumentException("missing provider");
        }
        final Object[] impl = SSLSecurity.getImpl(s, "SSLContext", s2);
        return new SSLContext((SSLContextSpi)impl[0], (Provider)impl[1], s);
    }
    
    public static SSLContext getInstance(final String s, final Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("missing provider");
        }
        final Object[] impl = SSLSecurity.getImpl(s, "SSLContext", provider);
        return new SSLContext((SSLContextSpi)impl[0], (Provider)impl[1], s);
    }
    
    public final String getProtocol() {
        return this.protocol;
    }
    
    public final Provider getProvider() {
        return this.provider;
    }
    
    public final void init(final KeyManager[] array, final TrustManager[] array2, final SecureRandom secureRandom) throws KeyManagementException {
        this.contextSpi.engineInit(array, array2, secureRandom);
    }
    
    public final SSLSocketFactory getSocketFactory() {
        return this.contextSpi.engineGetSocketFactory();
    }
    
    public final SSLServerSocketFactory getServerSocketFactory() {
        return this.contextSpi.engineGetServerSocketFactory();
    }
}
