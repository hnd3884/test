package org.apache.xml.security.keys.storage.implementations;

import java.security.KeyStoreException;
import java.util.Enumeration;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import java.security.cert.X509Certificate;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.xml.security.keys.storage.StorageResolverException;
import java.util.Iterator;
import java.security.KeyStore;
import org.apache.xml.security.keys.storage.StorageResolverSpi;

public class KeyStoreResolver extends StorageResolverSpi
{
    KeyStore _keyStore;
    Iterator _iterator;
    
    public KeyStoreResolver(final KeyStore keyStore) throws StorageResolverException {
        this._keyStore = null;
        this._iterator = null;
        this._keyStore = keyStore;
        this._iterator = new KeyStoreIterator(this._keyStore);
    }
    
    public Iterator getIterator() {
        return this._iterator;
    }
    
    public static void main(final String[] array) throws Exception {
        final KeyStore instance = KeyStore.getInstance(KeyStore.getDefaultType());
        instance.load(new FileInputStream("data/org/apache/xml/security/samples/input/keystore.jks"), "xmlsecurity".toCharArray());
        final Iterator iterator = new KeyStoreResolver(instance).getIterator();
        while (iterator.hasNext()) {
            System.out.println(Base64.encode(XMLX509SKI.getSKIBytesFromCert((X509Certificate)iterator.next())));
        }
    }
    
    static class KeyStoreIterator implements Iterator
    {
        KeyStore _keyStore;
        Enumeration _aliases;
        
        public KeyStoreIterator(final KeyStore keyStore) throws StorageResolverException {
            this._keyStore = null;
            this._aliases = null;
            try {
                this._keyStore = keyStore;
                this._aliases = this._keyStore.aliases();
            }
            catch (final KeyStoreException ex) {
                throw new StorageResolverException("generic.EmptyMessage", ex);
            }
        }
        
        public boolean hasNext() {
            return this._aliases.hasMoreElements();
        }
        
        public Object next() {
            final String s = this._aliases.nextElement();
            try {
                return this._keyStore.getCertificate(s);
            }
            catch (final KeyStoreException ex) {
                return null;
            }
        }
        
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
    }
}
