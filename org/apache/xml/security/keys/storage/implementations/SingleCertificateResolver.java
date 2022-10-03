package org.apache.xml.security.keys.storage.implementations;

import java.util.Iterator;
import java.security.cert.X509Certificate;
import org.apache.xml.security.keys.storage.StorageResolverSpi;

public class SingleCertificateResolver extends StorageResolverSpi
{
    X509Certificate _certificate;
    Iterator _iterator;
    
    public SingleCertificateResolver(final X509Certificate certificate) {
        this._certificate = null;
        this._iterator = null;
        this._certificate = certificate;
        this._iterator = new InternalIterator(this._certificate);
    }
    
    public Iterator getIterator() {
        return this._iterator;
    }
    
    static class InternalIterator implements Iterator
    {
        boolean _alreadyReturned;
        X509Certificate _certificate;
        
        public InternalIterator(final X509Certificate certificate) {
            this._alreadyReturned = false;
            this._certificate = null;
            this._certificate = certificate;
        }
        
        public boolean hasNext() {
            return !this._alreadyReturned;
        }
        
        public Object next() {
            this._alreadyReturned = true;
            return this._certificate;
        }
        
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
    }
}
