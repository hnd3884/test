package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.PrivateKey;
import java.security.Key;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import java.security.KeyStore;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class SecretKeyResolver extends KeyResolverSpi
{
    private static final Logger LOG;
    private KeyStore keyStore;
    private char[] password;
    
    public SecretKeyResolver(final KeyStore keyStore, final char[] password) {
        this.keyStore = keyStore;
        this.password = password;
    }
    
    @Override
    public boolean engineCanResolve(final Element element, final String s, final StorageResolver storageResolver) {
        return XMLUtils.elementIsInSignatureSpace(element, "KeyName");
    }
    
    @Override
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    @Override
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    @Override
    public SecretKey engineResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        SecretKeyResolver.LOG.debug("Can I resolve {}?", element.getTagName());
        if (XMLUtils.elementIsInSignatureSpace(element, "KeyName")) {
            final String nodeValue = element.getFirstChild().getNodeValue();
            try {
                final Key key = this.keyStore.getKey(nodeValue, this.password);
                if (key instanceof SecretKey) {
                    return (SecretKey)key;
                }
            }
            catch (final Exception ex) {
                SecretKeyResolver.LOG.debug("Cannot recover the key", ex);
            }
        }
        SecretKeyResolver.LOG.debug("I can't");
        return null;
    }
    
    @Override
    public PrivateKey engineLookupAndResolvePrivateKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SecretKeyResolver.class);
    }
}
