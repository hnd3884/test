package org.apache.xml.security.keys.storage;

import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.keys.storage.implementations.SingleCertificateResolver;
import java.security.cert.X509Certificate;
import org.apache.xml.security.keys.storage.implementations.KeyStoreResolver;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;

public class StorageResolver
{
    static Log log;
    List _storageResolvers;
    Iterator _iterator;
    
    public StorageResolver() {
        this._storageResolvers = null;
        this._iterator = null;
    }
    
    public StorageResolver(final StorageResolverSpi storageResolverSpi) {
        this._storageResolvers = null;
        this._iterator = null;
        this.add(storageResolverSpi);
    }
    
    public void add(final StorageResolverSpi storageResolverSpi) {
        if (this._storageResolvers == null) {
            this._storageResolvers = new ArrayList();
        }
        this._storageResolvers.add(storageResolverSpi);
        this._iterator = null;
    }
    
    public StorageResolver(final KeyStore keyStore) {
        this._storageResolvers = null;
        this._iterator = null;
        this.add(keyStore);
    }
    
    public void add(final KeyStore keyStore) {
        try {
            this.add(new KeyStoreResolver(keyStore));
        }
        catch (final StorageResolverException ex) {
            StorageResolver.log.error((Object)"Could not add KeyStore because of: ", (Throwable)ex);
        }
    }
    
    public StorageResolver(final X509Certificate x509Certificate) {
        this._storageResolvers = null;
        this._iterator = null;
        this.add(x509Certificate);
    }
    
    public void add(final X509Certificate x509Certificate) {
        this.add(new SingleCertificateResolver(x509Certificate));
    }
    
    public Iterator getIterator() {
        if (this._iterator == null) {
            if (this._storageResolvers == null) {
                this._storageResolvers = new ArrayList();
            }
            this._iterator = new StorageResolverIterator(this._storageResolvers.iterator());
        }
        return this._iterator;
    }
    
    public boolean hasNext() {
        if (this._iterator == null) {
            if (this._storageResolvers == null) {
                this._storageResolvers = new ArrayList();
            }
            this._iterator = new StorageResolverIterator(this._storageResolvers.iterator());
        }
        return this._iterator.hasNext();
    }
    
    public X509Certificate next() {
        return this._iterator.next();
    }
    
    static {
        StorageResolver.log = LogFactory.getLog(StorageResolver.class.getName());
    }
    
    static class StorageResolverIterator implements Iterator
    {
        Iterator _resolvers;
        
        public StorageResolverIterator(final Iterator resolvers) {
            this._resolvers = null;
            this._resolvers = resolvers;
        }
        
        public boolean hasNext() {
            return this._resolvers.hasNext();
        }
        
        public Object next() {
            return this._resolvers.next();
        }
        
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
    }
}
