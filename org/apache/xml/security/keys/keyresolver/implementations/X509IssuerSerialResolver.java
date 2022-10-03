package org.apache.xml.security.keys.keyresolver.implementations;

import org.apache.commons.logging.LogFactory;
import javax.crypto.SecretKey;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;

public class X509IssuerSerialResolver extends KeyResolverSpi
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
        if (X509IssuerSerialResolver.log.isDebugEnabled()) {
            X509IssuerSerialResolver.log.debug((Object)("Can I resolve " + element.getTagName() + "?"));
        }
        X509Data x509Data;
        try {
            x509Data = new X509Data(element, s);
        }
        catch (final XMLSignatureException ex) {
            X509IssuerSerialResolver.log.debug((Object)"I can't");
            return null;
        }
        catch (final XMLSecurityException ex2) {
            X509IssuerSerialResolver.log.debug((Object)"I can't");
            return null;
        }
        if (x509Data == null) {
            X509IssuerSerialResolver.log.debug((Object)"I can't");
            return null;
        }
        if (!x509Data.containsIssuerSerial()) {
            return null;
        }
        try {
            if (storageResolver == null) {
                final KeyResolverException ex3 = new KeyResolverException("KeyResolver.needStorageResolver", new Object[] { "X509IssuerSerial" });
                X509IssuerSerialResolver.log.info((Object)"", (Throwable)ex3);
                throw ex3;
            }
            final int lengthIssuerSerial = x509Data.lengthIssuerSerial();
            while (storageResolver.hasNext()) {
                final X509Certificate next = storageResolver.next();
                final XMLX509IssuerSerial xmlx509IssuerSerial = new XMLX509IssuerSerial(element.getOwnerDocument(), next);
                if (X509IssuerSerialResolver.log.isDebugEnabled()) {
                    X509IssuerSerialResolver.log.debug((Object)("Found Certificate Issuer: " + xmlx509IssuerSerial.getIssuerName()));
                    X509IssuerSerialResolver.log.debug((Object)("Found Certificate Serial: " + xmlx509IssuerSerial.getSerialNumber().toString()));
                }
                for (int i = 0; i < lengthIssuerSerial; ++i) {
                    final XMLX509IssuerSerial itemIssuerSerial = x509Data.itemIssuerSerial(i);
                    if (X509IssuerSerialResolver.log.isDebugEnabled()) {
                        X509IssuerSerialResolver.log.debug((Object)("Found Element Issuer:     " + itemIssuerSerial.getIssuerName()));
                        X509IssuerSerialResolver.log.debug((Object)("Found Element Serial:     " + itemIssuerSerial.getSerialNumber().toString()));
                    }
                    if (xmlx509IssuerSerial.equals(itemIssuerSerial)) {
                        X509IssuerSerialResolver.log.debug((Object)"match !!! ");
                        return next;
                    }
                    X509IssuerSerialResolver.log.debug((Object)"no match...");
                }
            }
            return null;
        }
        catch (final XMLSecurityException ex4) {
            X509IssuerSerialResolver.log.debug((Object)"XMLSecurityException", (Throwable)ex4);
            throw new KeyResolverException("generic.EmptyMessage", ex4);
        }
    }
    
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    static {
        X509IssuerSerialResolver.log = LogFactory.getLog(X509IssuerSerialResolver.class.getName());
    }
}
