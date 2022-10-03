package com.sun.net.ssl;

import java.security.UnrecoverableKeyException;
import java.security.KeyStoreException;
import java.security.KeyStore;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import java.security.Provider;

@Deprecated
public class KeyManagerFactory
{
    private Provider provider;
    private KeyManagerFactorySpi factorySpi;
    private String algorithm;
    
    public static final String getDefaultAlgorithm() {
        String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Security.getProperty("sun.ssl.keymanager.type");
            }
        });
        if (s == null) {
            s = "SunX509";
        }
        return s;
    }
    
    protected KeyManagerFactory(final KeyManagerFactorySpi factorySpi, final Provider provider, final String algorithm) {
        this.factorySpi = factorySpi;
        this.provider = provider;
        this.algorithm = algorithm;
    }
    
    public final String getAlgorithm() {
        return this.algorithm;
    }
    
    public static final KeyManagerFactory getInstance(final String s) throws NoSuchAlgorithmException {
        try {
            final Object[] impl = SSLSecurity.getImpl(s, "KeyManagerFactory", (String)null);
            return new KeyManagerFactory((KeyManagerFactorySpi)impl[0], (Provider)impl[1], s);
        }
        catch (final NoSuchProviderException ex) {
            throw new NoSuchAlgorithmException(s + " not found");
        }
    }
    
    public static final KeyManagerFactory getInstance(final String s, final String s2) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (s2 == null || s2.length() == 0) {
            throw new IllegalArgumentException("missing provider");
        }
        final Object[] impl = SSLSecurity.getImpl(s, "KeyManagerFactory", s2);
        return new KeyManagerFactory((KeyManagerFactorySpi)impl[0], (Provider)impl[1], s);
    }
    
    public static final KeyManagerFactory getInstance(final String s, final Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("missing provider");
        }
        final Object[] impl = SSLSecurity.getImpl(s, "KeyManagerFactory", provider);
        return new KeyManagerFactory((KeyManagerFactorySpi)impl[0], (Provider)impl[1], s);
    }
    
    public final Provider getProvider() {
        return this.provider;
    }
    
    public void init(final KeyStore keyStore, final char[] array) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.factorySpi.engineInit(keyStore, array);
    }
    
    public KeyManager[] getKeyManagers() {
        return this.factorySpi.engineGetKeyManagers();
    }
}
