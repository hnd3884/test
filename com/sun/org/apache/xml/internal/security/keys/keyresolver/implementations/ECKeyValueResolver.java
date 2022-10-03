package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.util.logging.Level;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.ECKeyValue;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import java.util.logging.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class ECKeyValueResolver extends KeyResolverSpi
{
    private static final Logger LOG;
    
    @Override
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) {
        if (element == null) {
            return null;
        }
        Element selectDs11Node = null;
        if (XMLUtils.elementIsInSignatureSpace(element, "KeyValue")) {
            selectDs11Node = XMLUtils.selectDs11Node(element.getFirstChild(), "ECKeyValue", 0);
        }
        else if (XMLUtils.elementIsInSignature11Space(element, "ECKeyValue")) {
            selectDs11Node = element;
        }
        if (selectDs11Node == null) {
            return null;
        }
        try {
            return new ECKeyValue(selectDs11Node, s).getPublicKey();
        }
        catch (final XMLSecurityException ex) {
            ECKeyValueResolver.LOG.log(Level.FINE, ex.getMessage(), ex);
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
        LOG = Logger.getLogger(ECKeyValueResolver.class.getName());
    }
}
