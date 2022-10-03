package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.DEREncodedKeyValue;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class DEREncodedKeyValueResolver extends KeyResolverSpi
{
    private static final Logger LOG;
    
    @Override
    public boolean engineCanResolve(final Element element, final String s, final StorageResolver storageResolver) {
        return XMLUtils.elementIsInSignature11Space(element, "DEREncodedKeyValue");
    }
    
    @Override
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        DEREncodedKeyValueResolver.LOG.debug("Can I resolve {}", element.getTagName());
        if (!this.engineCanResolve(element, s, storageResolver)) {
            return null;
        }
        try {
            return new DEREncodedKeyValue(element, s).getPublicKey();
        }
        catch (final XMLSecurityException ex) {
            DEREncodedKeyValueResolver.LOG.debug("XMLSecurityException", ex);
            return null;
        }
    }
    
    @Override
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    @Override
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    @Override
    public PrivateKey engineLookupAndResolvePrivateKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(DEREncodedKeyValueResolver.class);
    }
}
