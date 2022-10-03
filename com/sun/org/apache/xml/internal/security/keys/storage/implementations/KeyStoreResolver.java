package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.security.KeyStoreException;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException;
import java.security.KeyStore;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;

public class KeyStoreResolver extends StorageResolverSpi
{
    private KeyStore keyStore;
    
    public KeyStoreResolver(final KeyStore keyStore) throws StorageResolverException {
        this.keyStore = keyStore;
        try {
            keyStore.aliases();
        }
        catch (final KeyStoreException ex) {
            throw new StorageResolverException(ex);
        }
    }
    
    @Override
    public Iterator<Certificate> getIterator() {
        return new KeyStoreIterator(this.keyStore);
    }
    
    static class KeyStoreIterator implements Iterator<Certificate>
    {
        KeyStore keyStore;
        Enumeration<String> aliases;
        Certificate nextCert;
        
        public KeyStoreIterator(final KeyStore keyStore) {
            this.keyStore = null;
            this.aliases = null;
            this.nextCert = null;
            try {
                this.keyStore = keyStore;
                this.aliases = this.keyStore.aliases();
            }
            catch (final KeyStoreException ex) {
                this.aliases = new Enumeration<String>() {
                    @Override
                    public boolean hasMoreElements() {
                        return false;
                    }
                    
                    @Override
                    public String nextElement() {
                        return null;
                    }
                };
            }
        }
        
        @Override
        public boolean hasNext() {
            if (this.nextCert == null) {
                this.nextCert = this.findNextCert();
            }
            return this.nextCert != null;
        }
        
        @Override
        public Certificate next() {
            if (this.nextCert == null) {
                this.nextCert = this.findNextCert();
                if (this.nextCert == null) {
                    throw new NoSuchElementException();
                }
            }
            final Certificate nextCert = this.nextCert;
            this.nextCert = null;
            return nextCert;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
        
        private Certificate findNextCert() {
            while (this.aliases.hasMoreElements()) {
                final String s = this.aliases.nextElement();
                try {
                    final Certificate certificate = this.keyStore.getCertificate(s);
                    if (certificate != null) {
                        return certificate;
                    }
                    continue;
                }
                catch (final KeyStoreException ex) {
                    return null;
                }
            }
            return null;
        }
    }
}
