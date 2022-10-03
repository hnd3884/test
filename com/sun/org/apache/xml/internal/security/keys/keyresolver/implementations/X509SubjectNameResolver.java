package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import javax.crypto.SecretKey;
import java.security.cert.Certificate;
import java.util.Iterator;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SubjectName;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class X509SubjectNameResolver extends KeyResolverSpi
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
        X509SubjectNameResolver.LOG.debug("Can I resolve {}?", element.getTagName());
        if (!XMLUtils.elementIsInSignatureSpace(element, "X509Data")) {
            X509SubjectNameResolver.LOG.debug("I can't");
            return null;
        }
        final Element[] selectDsNodes = XMLUtils.selectDsNodes(element.getFirstChild(), "X509SubjectName");
        if (selectDsNodes == null || selectDsNodes.length <= 0) {
            X509SubjectNameResolver.LOG.debug("I can't");
            return null;
        }
        try {
            if (storageResolver == null) {
                final KeyResolverException ex = new KeyResolverException("KeyResolver.needStorageResolver", new Object[] { "X509SubjectName" });
                X509SubjectNameResolver.LOG.debug("", ex);
                throw ex;
            }
            final XMLX509SubjectName[] array = new XMLX509SubjectName[selectDsNodes.length];
            for (int i = 0; i < selectDsNodes.length; ++i) {
                array[i] = new XMLX509SubjectName(selectDsNodes[i], s);
            }
            final Iterator<Certificate> iterator = storageResolver.getIterator();
            while (iterator.hasNext()) {
                final X509Certificate x509Certificate = iterator.next();
                final XMLX509SubjectName xmlx509SubjectName = new XMLX509SubjectName(element.getOwnerDocument(), x509Certificate);
                X509SubjectNameResolver.LOG.debug("Found Certificate SN: {}", xmlx509SubjectName.getSubjectName());
                for (int j = 0; j < array.length; ++j) {
                    X509SubjectNameResolver.LOG.debug("Found Element SN:     {}", array[j].getSubjectName());
                    if (xmlx509SubjectName.equals(array[j])) {
                        X509SubjectNameResolver.LOG.debug("match !!! ");
                        return x509Certificate;
                    }
                    X509SubjectNameResolver.LOG.debug("no match...");
                }
            }
            return null;
        }
        catch (final XMLSecurityException ex2) {
            X509SubjectNameResolver.LOG.debug("XMLSecurityException", ex2);
            throw new KeyResolverException(ex2);
        }
    }
    
    @Override
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(X509SubjectNameResolver.class);
    }
}
