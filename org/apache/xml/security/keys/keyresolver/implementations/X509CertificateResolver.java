package org.apache.xml.security.keys.keyresolver.implementations;

import org.apache.commons.logging.LogFactory;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;

public class X509CertificateResolver extends KeyResolverSpi
{
    static Log log;
    
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        final X509Certificate engineLookupResolveX509Certificate = this.engineLookupResolveX509Certificate(element, s, storageResolver);
        if (engineLookupResolveX509Certificate != null) {
            return engineLookupResolveX509Certificate.getPublicKey();
        }
        return null;
    }
    
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        try {
            final Element[] selectDsNodes = XMLUtils.selectDsNodes(element.getFirstChild(), "X509Certificate");
            if (selectDsNodes != null && selectDsNodes.length != 0) {
                for (int i = 0; i < selectDsNodes.length; ++i) {
                    final X509Certificate x509Certificate = new XMLX509Certificate(selectDsNodes[i], s).getX509Certificate();
                    if (x509Certificate != null) {
                        return x509Certificate;
                    }
                }
                return null;
            }
            final Element selectDsNode = XMLUtils.selectDsNode(element.getFirstChild(), "X509Data", 0);
            if (selectDsNode != null) {
                return this.engineLookupResolveX509Certificate(selectDsNode, s, storageResolver);
            }
            return null;
        }
        catch (final XMLSecurityException ex) {
            X509CertificateResolver.log.debug((Object)"XMLSecurityException", (Throwable)ex);
            throw new KeyResolverException("generic.EmptyMessage", ex);
        }
    }
    
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    static {
        X509CertificateResolver.log = LogFactory.getLog(X509CertificateResolver.class.getName());
    }
}
