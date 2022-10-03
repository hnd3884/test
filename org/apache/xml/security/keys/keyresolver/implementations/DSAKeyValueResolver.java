package org.apache.xml.security.keys.keyresolver.implementations;

import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.keyvalues.DSAKeyValue;
import org.apache.xml.security.utils.XMLUtils;
import java.security.PublicKey;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;

public class DSAKeyValueResolver extends KeyResolverSpi
{
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) {
        if (element == null) {
            return null;
        }
        Element selectDsNode = null;
        final boolean elementIsInSignatureSpace = XMLUtils.elementIsInSignatureSpace(element, "KeyValue");
        final boolean elementIsInSignatureSpace2 = XMLUtils.elementIsInSignatureSpace(element, "DSAKeyValue");
        if (elementIsInSignatureSpace) {
            selectDsNode = XMLUtils.selectDsNode(element.getFirstChild(), "DSAKeyValue", 0);
        }
        else if (elementIsInSignatureSpace2) {
            selectDsNode = element;
        }
        if (selectDsNode == null) {
            return null;
        }
        try {
            return new DSAKeyValue(selectDsNode, s).getPublicKey();
        }
        catch (final XMLSecurityException ex) {
            return null;
        }
    }
    
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
}
