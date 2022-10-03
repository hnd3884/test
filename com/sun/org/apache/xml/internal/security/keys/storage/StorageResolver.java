package com.sun.org.apache.xml.internal.security.keys.storage;

import java.util.NoSuchElementException;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.cert.Certificate;
import java.util.Iterator;
import com.sun.org.apache.xml.internal.security.keys.storage.implementations.SingleCertificateResolver;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import com.sun.org.slf4j.internal.Logger;

public class StorageResolver
{
    private static final Logger LOG;
    private List<StorageResolverSpi> storageResolvers;
    
    public StorageResolver() {
    }
    
    public StorageResolver(final StorageResolverSpi storageResolverSpi) {
        this.add(storageResolverSpi);
    }
    
    public void add(final StorageResolverSpi storageResolverSpi) {
        if (this.storageResolvers == null) {
            this.storageResolvers = new ArrayList<StorageResolverSpi>();
        }
        this.storageResolvers.add(storageResolverSpi);
    }
    
    public StorageResolver(final KeyStore keyStore) {
        this.add(keyStore);
    }
    
    public void add(final KeyStore keyStore) {
        try {
            this.add(new KeyStoreResolver(keyStore));
        }
        catch (final StorageResolverException ex) {
            StorageResolver.LOG.error("Could not add KeyStore because of: ", ex);
        }
    }
    
    public StorageResolver(final X509Certificate x509Certificate) {
        this.add(x509Certificate);
    }
    
    public void add(final X509Certificate x509Certificate) {
        this.add(new SingleCertificateResolver(x509Certificate));
    }
    
    public Iterator<Certificate> getIterator() {
        return new StorageResolverIterator(this.storageResolvers.iterator());
    }
    
    static {
        LOG = LoggerFactory.getLogger(StorageResolver.class);
    }
    
    static class StorageResolverIterator implements Iterator<Certificate>
    {
        Iterator<StorageResolverSpi> resolvers;
        Iterator<Certificate> currentResolver;
        
        public StorageResolverIterator(final Iterator<StorageResolverSpi> resolvers) {
            this.resolvers = null;
            this.currentResolver = null;
            this.resolvers = resolvers;
            this.currentResolver = this.findNextResolver();
        }
        
        @Override
        public boolean hasNext() {
            if (this.currentResolver == null) {
                return false;
            }
            if (this.currentResolver.hasNext()) {
                return true;
            }
            this.currentResolver = this.findNextResolver();
            return this.currentResolver != null;
        }
        
        @Override
        public Certificate next() {
            if (this.hasNext()) {
                return this.currentResolver.next();
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
        
        private Iterator<Certificate> findNextResolver() {
            while (this.resolvers.hasNext()) {
                final Iterator<Certificate> iterator = this.resolvers.next().getIterator();
                if (iterator.hasNext()) {
                    return iterator;
                }
            }
            return null;
        }
    }
}
