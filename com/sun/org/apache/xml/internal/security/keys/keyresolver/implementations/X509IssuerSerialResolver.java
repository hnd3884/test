package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import javax.crypto.SecretKey;
import java.security.cert.Certificate;
import java.util.Iterator;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class X509IssuerSerialResolver extends KeyResolverSpi
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
        X509IssuerSerialResolver.LOG.debug("Can I resolve {}?", element.getTagName());
        X509Data x509Data;
        try {
            x509Data = new X509Data(element, s);
        }
        catch (final XMLSignatureException ex) {
            X509IssuerSerialResolver.LOG.debug("I can't");
            return null;
        }
        catch (final XMLSecurityException ex2) {
            X509IssuerSerialResolver.LOG.debug("I can't");
            return null;
        }
        if (!x509Data.containsIssuerSerial()) {
            return null;
        }
        try {
            if (storageResolver == null) {
                final KeyResolverException ex3 = new KeyResolverException("KeyResolver.needStorageResolver", new Object[] { "X509IssuerSerial" });
                X509IssuerSerialResolver.LOG.debug("", ex3);
                throw ex3;
            }
            final int lengthIssuerSerial = x509Data.lengthIssuerSerial();
            final Iterator<Certificate> iterator = storageResolver.getIterator();
            while (iterator.hasNext()) {
                final X509Certificate x509Certificate = iterator.next();
                final XMLX509IssuerSerial xmlx509IssuerSerial = new XMLX509IssuerSerial(element.getOwnerDocument(), x509Certificate);
                X509IssuerSerialResolver.LOG.debug("Found Certificate Issuer: {}", xmlx509IssuerSerial.getIssuerName());
                X509IssuerSerialResolver.LOG.debug("Found Certificate Serial: {}", xmlx509IssuerSerial.getSerialNumber().toString());
                for (int i = 0; i < lengthIssuerSerial; ++i) {
                    final XMLX509IssuerSerial itemIssuerSerial = x509Data.itemIssuerSerial(i);
                    X509IssuerSerialResolver.LOG.debug("Found Element Issuer:     {}", itemIssuerSerial.getIssuerName());
                    X509IssuerSerialResolver.LOG.debug("Found Element Serial:     {}", itemIssuerSerial.getSerialNumber().toString());
                    if (xmlx509IssuerSerial.equals(itemIssuerSerial)) {
                        X509IssuerSerialResolver.LOG.debug("match !!! ");
                        return x509Certificate;
                    }
                    X509IssuerSerialResolver.LOG.debug("no match...");
                }
            }
            return null;
        }
        catch (final XMLSecurityException ex4) {
            X509IssuerSerialResolver.LOG.debug("XMLSecurityException", ex4);
            throw new KeyResolverException(ex4);
        }
    }
    
    @Override
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(X509IssuerSerialResolver.class);
    }
}
