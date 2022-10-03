package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.InputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import java.util.Map;

public abstract class KeyResolverSpi
{
    protected Map<String, String> properties;
    protected boolean globalResolver;
    protected boolean secureValidation;
    
    public KeyResolverSpi() {
        this.globalResolver = false;
    }
    
    public void setSecureValidation(final boolean secureValidation) {
        this.secureValidation = secureValidation;
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
        if (this.globalResolver) {
            try {
                return (KeyResolverSpi)this.getClass().newInstance();
            }
            catch (final InstantiationException ex) {
                throw new KeyResolverException(ex, "");
            }
            catch (final IllegalAccessException ex2) {
                throw new KeyResolverException(ex2, "");
            }
        }
        return this;
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
    
    public PrivateKey engineLookupAndResolvePrivateKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    public void engineSetProperty(final String s, final String s2) {
        if (this.properties == null) {
            this.properties = new HashMap<String, String>();
        }
        this.properties.put(s, s2);
    }
    
    public String engineGetProperty(final String s) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(s);
    }
    
    public boolean understandsProperty(final String s) {
        return this.properties != null && this.properties.get(s) != null;
    }
    
    public void setGlobalResolver(final boolean globalResolver) {
        this.globalResolver = globalResolver;
    }
    
    protected static Element getDocFromBytes(final byte[] array, final boolean b) throws KeyResolverException {
        try (final ByteArrayInputStream is = new ByteArrayInputStream(array)) {
            return XMLUtils.createDocumentBuilder(false, b).parse(is).getDocumentElement();
        }
        catch (final SAXException ex) {
            throw new KeyResolverException(ex);
        }
        catch (final IOException ex2) {
            throw new KeyResolverException(ex2);
        }
        catch (final ParserConfigurationException ex3) {
            throw new KeyResolverException(ex3);
        }
    }
}
