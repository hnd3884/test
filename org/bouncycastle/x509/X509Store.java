package org.bouncycastle.x509;

import java.util.Collection;
import org.bouncycastle.util.Selector;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import org.bouncycastle.util.Store;

public class X509Store implements Store
{
    private Provider _provider;
    private X509StoreSpi _spi;
    
    public static X509Store getInstance(final String s, final X509StoreParameters x509StoreParameters) throws NoSuchStoreException {
        try {
            return createStore(X509Util.getImplementation("X509Store", s), x509StoreParameters);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new NoSuchStoreException(ex.getMessage());
        }
    }
    
    public static X509Store getInstance(final String s, final X509StoreParameters x509StoreParameters, final String s2) throws NoSuchStoreException, NoSuchProviderException {
        return getInstance(s, x509StoreParameters, X509Util.getProvider(s2));
    }
    
    public static X509Store getInstance(final String s, final X509StoreParameters x509StoreParameters, final Provider provider) throws NoSuchStoreException {
        try {
            return createStore(X509Util.getImplementation("X509Store", s, provider), x509StoreParameters);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new NoSuchStoreException(ex.getMessage());
        }
    }
    
    private static X509Store createStore(final X509Util.Implementation implementation, final X509StoreParameters x509StoreParameters) {
        final X509StoreSpi x509StoreSpi = (X509StoreSpi)implementation.getEngine();
        x509StoreSpi.engineInit(x509StoreParameters);
        return new X509Store(implementation.getProvider(), x509StoreSpi);
    }
    
    private X509Store(final Provider provider, final X509StoreSpi spi) {
        this._provider = provider;
        this._spi = spi;
    }
    
    public Provider getProvider() {
        return this._provider;
    }
    
    public Collection getMatches(final Selector selector) {
        return this._spi.engineGetMatches(selector);
    }
}
