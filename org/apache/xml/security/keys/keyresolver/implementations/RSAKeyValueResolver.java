package org.apache.xml.security.keys.keyresolver.implementations;

import org.apache.commons.logging.LogFactory;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.keyvalues.RSAKeyValue;
import org.apache.xml.security.utils.XMLUtils;
import java.security.PublicKey;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;

public class RSAKeyValueResolver extends KeyResolverSpi
{
    static Log log;
    
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) {
        if (RSAKeyValueResolver.log.isDebugEnabled()) {
            RSAKeyValueResolver.log.debug((Object)("Can I resolve " + element.getTagName()));
        }
        if (element == null) {
            return null;
        }
        final boolean elementIsInSignatureSpace = XMLUtils.elementIsInSignatureSpace(element, "KeyValue");
        final boolean elementIsInSignatureSpace2 = XMLUtils.elementIsInSignatureSpace(element, "RSAKeyValue");
        Element selectDsNode = null;
        if (elementIsInSignatureSpace) {
            selectDsNode = XMLUtils.selectDsNode(element.getFirstChild(), "RSAKeyValue", 0);
        }
        else if (elementIsInSignatureSpace2) {
            selectDsNode = element;
        }
        if (selectDsNode == null) {
            return null;
        }
        try {
            return new RSAKeyValue(selectDsNode, s).getPublicKey();
        }
        catch (final XMLSecurityException ex) {
            RSAKeyValueResolver.log.debug((Object)"XMLSecurityException", (Throwable)ex);
            return null;
        }
    }
    
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    static {
        RSAKeyValueResolver.log = LogFactory.getLog(RSAKeyValueResolver.class.getName());
    }
}
