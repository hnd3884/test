package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import javax.xml.namespace.QName;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
import com.sun.org.apache.xml.internal.security.keys.content.KeyInfoReference;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class KeyInfoReferenceResolver extends KeyResolverSpi
{
    private static final Logger LOG;
    
    @Override
    public boolean engineCanResolve(final Element element, final String s, final StorageResolver storageResolver) {
        return XMLUtils.elementIsInSignature11Space(element, "KeyInfoReference");
    }
    
    @Override
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        KeyInfoReferenceResolver.LOG.debug("Can I resolve {}", element.getTagName());
        if (!this.engineCanResolve(element, s, storageResolver)) {
            return null;
        }
        try {
            final KeyInfo resolveReferentKeyInfo = this.resolveReferentKeyInfo(element, s, storageResolver);
            if (resolveReferentKeyInfo != null) {
                return resolveReferentKeyInfo.getPublicKey();
            }
        }
        catch (final XMLSecurityException ex) {
            KeyInfoReferenceResolver.LOG.debug("XMLSecurityException", ex);
        }
        return null;
    }
    
    @Override
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        KeyInfoReferenceResolver.LOG.debug("Can I resolve {}", element.getTagName());
        if (!this.engineCanResolve(element, s, storageResolver)) {
            return null;
        }
        try {
            final KeyInfo resolveReferentKeyInfo = this.resolveReferentKeyInfo(element, s, storageResolver);
            if (resolveReferentKeyInfo != null) {
                return resolveReferentKeyInfo.getX509Certificate();
            }
        }
        catch (final XMLSecurityException ex) {
            KeyInfoReferenceResolver.LOG.debug("XMLSecurityException", ex);
        }
        return null;
    }
    
    @Override
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        KeyInfoReferenceResolver.LOG.debug("Can I resolve {}", element.getTagName());
        if (!this.engineCanResolve(element, s, storageResolver)) {
            return null;
        }
        try {
            final KeyInfo resolveReferentKeyInfo = this.resolveReferentKeyInfo(element, s, storageResolver);
            if (resolveReferentKeyInfo != null) {
                return resolveReferentKeyInfo.getSecretKey();
            }
        }
        catch (final XMLSecurityException ex) {
            KeyInfoReferenceResolver.LOG.debug("XMLSecurityException", ex);
        }
        return null;
    }
    
    @Override
    public PrivateKey engineLookupAndResolvePrivateKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        KeyInfoReferenceResolver.LOG.debug("Can I resolve " + element.getTagName());
        if (!this.engineCanResolve(element, s, storageResolver)) {
            return null;
        }
        try {
            final KeyInfo resolveReferentKeyInfo = this.resolveReferentKeyInfo(element, s, storageResolver);
            if (resolveReferentKeyInfo != null) {
                return resolveReferentKeyInfo.getPrivateKey();
            }
        }
        catch (final XMLSecurityException ex) {
            KeyInfoReferenceResolver.LOG.debug("XMLSecurityException", ex);
        }
        return null;
    }
    
    private KeyInfo resolveReferentKeyInfo(final Element element, final String s, final StorageResolver storageResolver) throws XMLSecurityException {
        final Attr uriAttr = new KeyInfoReference(element, s).getURIAttr();
        final XMLSignatureInput resolveInput = this.resolveInput(uriAttr, s, this.secureValidation);
        Element obtainReferenceElement;
        try {
            obtainReferenceElement = this.obtainReferenceElement(resolveInput);
        }
        catch (final Exception ex) {
            KeyInfoReferenceResolver.LOG.debug("XMLSecurityException", ex);
            return null;
        }
        if (obtainReferenceElement == null) {
            KeyInfoReferenceResolver.LOG.debug("De-reference of KeyInfoReference URI returned null: {}", uriAttr.getValue());
            return null;
        }
        this.validateReference(obtainReferenceElement);
        final KeyInfo keyInfo = new KeyInfo(obtainReferenceElement, s);
        keyInfo.addStorageResolver(storageResolver);
        return keyInfo;
    }
    
    private void validateReference(final Element element) throws XMLSecurityException {
        if (!XMLUtils.elementIsInSignatureSpace(element, "KeyInfo")) {
            throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.WrongType", new Object[] { new QName(element.getNamespaceURI(), element.getLocalName()) });
        }
        if (!new KeyInfo(element, "").containsKeyInfoReference()) {
            return;
        }
        if (this.secureValidation) {
            throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithSecure");
        }
        throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithoutSecure");
    }
    
    private XMLSignatureInput resolveInput(final Attr attr, final String s, final boolean b) throws XMLSecurityException {
        return ResourceResolver.getInstance(attr, s, b).resolve(attr, s, b);
    }
    
    private Element obtainReferenceElement(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException {
        Element docFromBytes;
        if (xmlSignatureInput.isElement()) {
            docFromBytes = (Element)xmlSignatureInput.getSubNode();
        }
        else {
            if (xmlSignatureInput.isNodeSet()) {
                KeyInfoReferenceResolver.LOG.debug("De-reference of KeyInfoReference returned an unsupported NodeSet");
                return null;
            }
            docFromBytes = KeyResolverSpi.getDocFromBytes(xmlSignatureInput.getBytes(), this.secureValidation);
        }
        return docFromBytes;
    }
    
    static {
        LOG = LoggerFactory.getLogger(KeyInfoReferenceResolver.class);
    }
}
