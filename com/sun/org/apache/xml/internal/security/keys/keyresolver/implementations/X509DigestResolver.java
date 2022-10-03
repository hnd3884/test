package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.Arrays;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Digest;
import javax.crypto.SecretKey;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class X509DigestResolver extends KeyResolverSpi
{
    private static final Logger LOG;
    
    @Override
    public boolean engineCanResolve(final Element element, final String s, final StorageResolver storageResolver) {
        if (XMLUtils.elementIsInSignatureSpace(element, "X509Data")) {
            try {
                return new X509Data(element, s).containsDigest();
            }
            catch (final XMLSecurityException ex) {
                return false;
            }
        }
        return false;
    }
    
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
        X509DigestResolver.LOG.debug("Can I resolve {}", element.getTagName());
        if (!this.engineCanResolve(element, s, storageResolver)) {
            return null;
        }
        try {
            return this.resolveCertificate(element, s, storageResolver);
        }
        catch (final XMLSecurityException ex) {
            X509DigestResolver.LOG.debug("XMLSecurityException", ex);
            return null;
        }
    }
    
    @Override
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    private X509Certificate resolveCertificate(final Element element, final String s, final StorageResolver storageResolver) throws XMLSecurityException {
        final Element[] selectDs11Nodes = XMLUtils.selectDs11Nodes(element.getFirstChild(), "X509Digest");
        if (selectDs11Nodes == null || selectDs11Nodes.length <= 0) {
            return null;
        }
        try {
            this.checkStorage(storageResolver);
            final XMLX509Digest[] array = new XMLX509Digest[selectDs11Nodes.length];
            for (int i = 0; i < selectDs11Nodes.length; ++i) {
                array[i] = new XMLX509Digest(selectDs11Nodes[i], s);
            }
            final Iterator<Certificate> iterator = storageResolver.getIterator();
            while (iterator.hasNext()) {
                final X509Certificate x509Certificate = iterator.next();
                for (int j = 0; j < array.length; ++j) {
                    final XMLX509Digest xmlx509Digest = array[j];
                    if (Arrays.equals(xmlx509Digest.getDigestBytes(), XMLX509Digest.getDigestBytesFromCert(x509Certificate, xmlx509Digest.getAlgorithm()))) {
                        X509DigestResolver.LOG.debug("Found certificate with: {}", x509Certificate.getSubjectX500Principal().getName());
                        return x509Certificate;
                    }
                }
            }
        }
        catch (final XMLSecurityException ex) {
            throw new KeyResolverException(ex);
        }
        return null;
    }
    
    private void checkStorage(final StorageResolver storageResolver) throws KeyResolverException {
        if (storageResolver == null) {
            final KeyResolverException ex = new KeyResolverException("KeyResolver.needStorageResolver", new Object[] { "X509Digest" });
            X509DigestResolver.LOG.debug("", ex);
            throw ex;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(X509DigestResolver.class);
    }
}
