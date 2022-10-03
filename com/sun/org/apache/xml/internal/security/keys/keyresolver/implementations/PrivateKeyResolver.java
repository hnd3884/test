package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.util.Arrays;
import java.security.cert.CertificateEncodingException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SubjectName;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import java.security.cert.Certificate;
import java.util.Enumeration;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import java.security.KeyStoreException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import java.security.KeyStore;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class PrivateKeyResolver extends KeyResolverSpi
{
    private static final Logger LOG;
    private KeyStore keyStore;
    private char[] password;
    
    public PrivateKeyResolver(final KeyStore keyStore, final char[] password) {
        this.keyStore = keyStore;
        this.password = password;
    }
    
    @Override
    public boolean engineCanResolve(final Element element, final String s, final StorageResolver storageResolver) {
        return XMLUtils.elementIsInSignatureSpace(element, "X509Data") || XMLUtils.elementIsInSignatureSpace(element, "KeyName");
    }
    
    @Override
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    @Override
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    @Override
    public SecretKey engineResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    @Override
    public PrivateKey engineLookupAndResolvePrivateKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        PrivateKeyResolver.LOG.debug("Can I resolve {}?", element.getTagName());
        if (XMLUtils.elementIsInSignatureSpace(element, "X509Data")) {
            final PrivateKey resolveX509Data = this.resolveX509Data(element, s);
            if (resolveX509Data != null) {
                return resolveX509Data;
            }
        }
        else if (XMLUtils.elementIsInSignatureSpace(element, "KeyName")) {
            PrivateKeyResolver.LOG.debug("Can I resolve KeyName?");
            final String nodeValue = element.getFirstChild().getNodeValue();
            try {
                final Key key = this.keyStore.getKey(nodeValue, this.password);
                if (key instanceof PrivateKey) {
                    return (PrivateKey)key;
                }
            }
            catch (final Exception ex) {
                PrivateKeyResolver.LOG.debug("Cannot recover the key", ex);
            }
        }
        PrivateKeyResolver.LOG.debug("I can't");
        return null;
    }
    
    private PrivateKey resolveX509Data(final Element element, final String s) {
        PrivateKeyResolver.LOG.debug("Can I resolve X509Data?");
        try {
            final X509Data x509Data = new X509Data(element, s);
            for (int lengthSKI = x509Data.lengthSKI(), i = 0; i < lengthSKI; ++i) {
                final PrivateKey resolveX509SKI = this.resolveX509SKI(x509Data.itemSKI(i));
                if (resolveX509SKI != null) {
                    return resolveX509SKI;
                }
            }
            for (int lengthIssuerSerial = x509Data.lengthIssuerSerial(), j = 0; j < lengthIssuerSerial; ++j) {
                final PrivateKey resolveX509IssuerSerial = this.resolveX509IssuerSerial(x509Data.itemIssuerSerial(j));
                if (resolveX509IssuerSerial != null) {
                    return resolveX509IssuerSerial;
                }
            }
            for (int lengthSubjectName = x509Data.lengthSubjectName(), k = 0; k < lengthSubjectName; ++k) {
                final PrivateKey resolveX509SubjectName = this.resolveX509SubjectName(x509Data.itemSubjectName(k));
                if (resolveX509SubjectName != null) {
                    return resolveX509SubjectName;
                }
            }
            for (int lengthCertificate = x509Data.lengthCertificate(), l = 0; l < lengthCertificate; ++l) {
                final PrivateKey resolveX509Certificate = this.resolveX509Certificate(x509Data.itemCertificate(l));
                if (resolveX509Certificate != null) {
                    return resolveX509Certificate;
                }
            }
        }
        catch (final XMLSecurityException ex) {
            PrivateKeyResolver.LOG.debug("XMLSecurityException", ex);
        }
        catch (final KeyStoreException ex2) {
            PrivateKeyResolver.LOG.debug("KeyStoreException", ex2);
        }
        return null;
    }
    
    private PrivateKey resolveX509SKI(final XMLX509SKI xmlx509SKI) throws XMLSecurityException, KeyStoreException {
        PrivateKeyResolver.LOG.debug("Can I resolve X509SKI?");
        final Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            final String s = aliases.nextElement();
            if (this.keyStore.isKeyEntry(s)) {
                final Certificate certificate = this.keyStore.getCertificate(s);
                if (!(certificate instanceof X509Certificate) || !new XMLX509SKI(xmlx509SKI.getDocument(), (X509Certificate)certificate).equals(xmlx509SKI)) {
                    continue;
                }
                PrivateKeyResolver.LOG.debug("match !!! ");
                try {
                    final Key key = this.keyStore.getKey(s, this.password);
                    if (key instanceof PrivateKey) {
                        return (PrivateKey)key;
                    }
                    continue;
                }
                catch (final Exception ex) {
                    PrivateKeyResolver.LOG.debug("Cannot recover the key", ex);
                }
            }
        }
        return null;
    }
    
    private PrivateKey resolveX509IssuerSerial(final XMLX509IssuerSerial xmlx509IssuerSerial) throws KeyStoreException {
        PrivateKeyResolver.LOG.debug("Can I resolve X509IssuerSerial?");
        final Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            final String s = aliases.nextElement();
            if (this.keyStore.isKeyEntry(s)) {
                final Certificate certificate = this.keyStore.getCertificate(s);
                if (!(certificate instanceof X509Certificate) || !new XMLX509IssuerSerial(xmlx509IssuerSerial.getDocument(), (X509Certificate)certificate).equals(xmlx509IssuerSerial)) {
                    continue;
                }
                PrivateKeyResolver.LOG.debug("match !!! ");
                try {
                    final Key key = this.keyStore.getKey(s, this.password);
                    if (key instanceof PrivateKey) {
                        return (PrivateKey)key;
                    }
                    continue;
                }
                catch (final Exception ex) {
                    PrivateKeyResolver.LOG.debug("Cannot recover the key", ex);
                }
            }
        }
        return null;
    }
    
    private PrivateKey resolveX509SubjectName(final XMLX509SubjectName xmlx509SubjectName) throws KeyStoreException {
        PrivateKeyResolver.LOG.debug("Can I resolve X509SubjectName?");
        final Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            final String s = aliases.nextElement();
            if (this.keyStore.isKeyEntry(s)) {
                final Certificate certificate = this.keyStore.getCertificate(s);
                if (!(certificate instanceof X509Certificate) || !new XMLX509SubjectName(xmlx509SubjectName.getDocument(), (X509Certificate)certificate).equals(xmlx509SubjectName)) {
                    continue;
                }
                PrivateKeyResolver.LOG.debug("match !!! ");
                try {
                    final Key key = this.keyStore.getKey(s, this.password);
                    if (key instanceof PrivateKey) {
                        return (PrivateKey)key;
                    }
                    continue;
                }
                catch (final Exception ex) {
                    PrivateKeyResolver.LOG.debug("Cannot recover the key", ex);
                }
            }
        }
        return null;
    }
    
    private PrivateKey resolveX509Certificate(final XMLX509Certificate xmlx509Certificate) throws XMLSecurityException, KeyStoreException {
        PrivateKeyResolver.LOG.debug("Can I resolve X509Certificate?");
        final byte[] certificateBytes = xmlx509Certificate.getCertificateBytes();
        final Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            final String s = aliases.nextElement();
            if (this.keyStore.isKeyEntry(s)) {
                final Certificate certificate = this.keyStore.getCertificate(s);
                if (!(certificate instanceof X509Certificate)) {
                    continue;
                }
                byte[] encoded = null;
                try {
                    encoded = certificate.getEncoded();
                }
                catch (final CertificateEncodingException ex) {
                    PrivateKeyResolver.LOG.debug("Cannot recover the key", ex);
                }
                if (encoded == null || !Arrays.equals(encoded, certificateBytes)) {
                    continue;
                }
                PrivateKeyResolver.LOG.debug("match !!! ");
                try {
                    final Key key = this.keyStore.getKey(s, this.password);
                    if (key instanceof PrivateKey) {
                        return (PrivateKey)key;
                    }
                    continue;
                }
                catch (final Exception ex2) {
                    PrivateKeyResolver.LOG.debug("Cannot recover the key", ex2);
                }
            }
        }
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(PrivateKeyResolver.class);
    }
}
