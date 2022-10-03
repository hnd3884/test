package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import javax.crypto.SecretKey;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class X509CertificateResolver extends KeyResolverSpi
{
    private static final Logger LOG;
    
    @Override
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        final X509Certificate engineLookupResolveX509Certificate = this.engineLookupResolveX509Certificate(element, s, storageResolver);
        if (engineLookupResolveX509Certificate != null) {
            return engineLookupResolveX509Certificate.getPublicKey();
        }
        return null;
    }
    
    @Override
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
            X509CertificateResolver.LOG.debug("Security Exception", ex);
            throw new KeyResolverException(ex);
        }
    }
    
    @Override
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(X509CertificateResolver.class);
    }
}
