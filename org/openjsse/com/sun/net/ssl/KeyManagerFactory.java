package org.openjsse.com.sun.net.ssl;

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
        String type = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Security.getProperty("sun.ssl.keymanager.type");
            }
        });
        if (type == null) {
            type = "SunX509";
        }
        return type;
    }
    
    protected KeyManagerFactory(final KeyManagerFactorySpi factorySpi, final Provider provider, final String algorithm) {
        this.factorySpi = factorySpi;
        this.provider = provider;
        this.algorithm = algorithm;
    }
    
    public final String getAlgorithm() {
        return this.algorithm;
    }
    
    public static final KeyManagerFactory getInstance(final String algorithm) throws NoSuchAlgorithmException {
        try {
            final Object[] objs = SSLSecurity.getImpl(algorithm, "KeyManagerFactory", (String)null);
            return new KeyManagerFactory((KeyManagerFactorySpi)objs[0], (Provider)objs[1], algorithm);
        }
        catch (final NoSuchProviderException e) {
            throw new NoSuchAlgorithmException(algorithm + " not found");
        }
    }
    
    public static final KeyManagerFactory getInstance(final String algorithm, final String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.length() == 0) {
            throw new IllegalArgumentException("missing provider");
        }
        final Object[] objs = SSLSecurity.getImpl(algorithm, "KeyManagerFactory", provider);
        return new KeyManagerFactory((KeyManagerFactorySpi)objs[0], (Provider)objs[1], algorithm);
    }
    
    public static final KeyManagerFactory getInstance(final String algorithm, final Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("missing provider");
        }
        final Object[] objs = SSLSecurity.getImpl(algorithm, "KeyManagerFactory", provider);
        return new KeyManagerFactory((KeyManagerFactorySpi)objs[0], (Provider)objs[1], algorithm);
    }
    
    public final Provider getProvider() {
        return this.provider;
    }
    
    public void init(final KeyStore ks, final char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.factorySpi.engineInit(ks, password);
    }
    
    public KeyManager[] getKeyManagers() {
        return this.factorySpi.engineGetKeyManagers();
    }
}
