package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import java.util.NoSuchElementException;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;

public class SingleCertificateResolver extends StorageResolverSpi
{
    private X509Certificate certificate;
    
    public SingleCertificateResolver(final X509Certificate certificate) {
        this.certificate = certificate;
    }
    
    @Override
    public Iterator<Certificate> getIterator() {
        return new InternalIterator(this.certificate);
    }
    
    static class InternalIterator implements Iterator<Certificate>
    {
        boolean alreadyReturned;
        X509Certificate certificate;
        
        public InternalIterator(final X509Certificate certificate) {
            this.alreadyReturned = false;
            this.certificate = null;
            this.certificate = certificate;
        }
        
        @Override
        public boolean hasNext() {
            return !this.alreadyReturned;
        }
        
        @Override
        public Certificate next() {
            if (this.alreadyReturned) {
                throw new NoSuchElementException();
            }
            this.alreadyReturned = true;
            return this.certificate;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
    }
}
