package org.apache.xml.security.keys.keyresolver;

import java.util.HashMap;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import java.util.Map;

public abstract class KeyResolverSpi
{
    protected Map _properties;
    protected boolean globalResolver;
    
    public KeyResolverSpi() {
        this._properties = null;
        this.globalResolver = false;
    }
    
    public boolean engineCanResolve(final Element element, final String s, final StorageResolver storageResolver) {
        throw new UnsupportedOperationException();
    }
    
    public PublicKey engineResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        throw new UnsupportedOperationException();
    }
    
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        final KeyResolverSpi cloneIfNeeded = this.cloneIfNeeded();
        if (!cloneIfNeeded.engineCanResolve(element, s, storageResolver)) {
            return null;
        }
        return cloneIfNeeded.engineResolvePublicKey(element, s, storageResolver);
    }
    
    private KeyResolverSpi cloneIfNeeded() throws KeyResolverException {
        KeyResolverSpi keyResolverSpi = this;
        if (this.globalResolver) {
            try {
                keyResolverSpi = (KeyResolverSpi)this.getClass().newInstance();
            }
            catch (final InstantiationException ex) {
                throw new KeyResolverException("", ex);
            }
            catch (final IllegalAccessException ex2) {
                throw new KeyResolverException("", ex2);
            }
        }
        return keyResolverSpi;
    }
    
    public X509Certificate engineResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        throw new UnsupportedOperationException();
    }
    
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        final KeyResolverSpi cloneIfNeeded = this.cloneIfNeeded();
        if (!cloneIfNeeded.engineCanResolve(element, s, storageResolver)) {
            return null;
        }
        return cloneIfNeeded.engineResolveX509Certificate(element, s, storageResolver);
    }
    
    public SecretKey engineResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        throw new UnsupportedOperationException();
    }
    
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        final KeyResolverSpi cloneIfNeeded = this.cloneIfNeeded();
        if (!cloneIfNeeded.engineCanResolve(element, s, storageResolver)) {
            return null;
        }
        return cloneIfNeeded.engineResolveSecretKey(element, s, storageResolver);
    }
    
    public void engineSetProperty(final String s, final String s2) {
        if (this._properties == null) {
            this._properties = new HashMap();
        }
        this._properties.put(s, s2);
    }
    
    public String engineGetProperty(final String s) {
        if (this._properties == null) {
            return null;
        }
        return this._properties.get(s);
    }
    
    public boolean understandsProperty(final String s) {
        return this._properties != null && this._properties.get(s) != null;
    }
    
    public void setGlobalResolver(final boolean globalResolver) {
        this.globalResolver = globalResolver;
    }
}
