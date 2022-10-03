package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class RSAKeyValueResolver extends KeyResolverSpi
{
    private static final Logger LOG;
    
    @Override
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) {
        if (element == null) {
            return null;
        }
        RSAKeyValueResolver.LOG.debug("Can I resolve {}", element.getTagName());
        final boolean elementIsInSignatureSpace = XMLUtils.elementIsInSignatureSpace(element, "KeyValue");
        Element selectDsNode = null;
        if (elementIsInSignatureSpace) {
            selectDsNode = XMLUtils.selectDsNode(element.getFirstChild(), "RSAKeyValue", 0);
        }
        else if (XMLUtils.elementIsInSignatureSpace(element, "RSAKeyValue")) {
            selectDsNode = element;
        }
        if (selectDsNode == null) {
            return null;
        }
        try {
            return new RSAKeyValue(selectDsNode, s).getPublicKey();
        }
        catch (final XMLSecurityException ex) {
            RSAKeyValueResolver.LOG.debug("XMLSecurityException", ex);
            return null;
        }
    }
    
    @Override
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    @Override
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(RSAKeyValueResolver.class);
    }
}
