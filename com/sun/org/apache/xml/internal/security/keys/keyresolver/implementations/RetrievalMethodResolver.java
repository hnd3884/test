package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.ArrayList;
import org.w3c.dom.Node;
import java.util.Set;
import javax.crypto.SecretKey;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import org.w3c.dom.Attr;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.cert.CertificateException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class RetrievalMethodResolver extends KeyResolverSpi
{
    private static final Logger LOG;
    
    @Override
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) {
        if (!XMLUtils.elementIsInSignatureSpace(element, "RetrievalMethod")) {
            return null;
        }
        try {
            final RetrievalMethod retrievalMethod = new RetrievalMethod(element, s);
            final String type = retrievalMethod.getType();
            final XMLSignatureInput resolveInput = resolveInput(retrievalMethod, s, this.secureValidation);
            if (!"http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(type)) {
                final Element obtainReferenceElement = obtainReferenceElement(resolveInput, this.secureValidation);
                if (XMLUtils.elementIsInSignatureSpace(obtainReferenceElement, "RetrievalMethod")) {
                    if (this.secureValidation) {
                        if (RetrievalMethodResolver.LOG.isDebugEnabled()) {
                            RetrievalMethodResolver.LOG.debug("Error: It is forbidden to have one RetrievalMethod point to another with secure validation");
                        }
                        return null;
                    }
                    if (obtainReferenceElement(resolveInput(new RetrievalMethod(obtainReferenceElement, s), s, this.secureValidation), this.secureValidation) == element) {
                        RetrievalMethodResolver.LOG.debug("Error: Can't have RetrievalMethods pointing to each other");
                        return null;
                    }
                }
                return resolveKey(obtainReferenceElement, s, storageResolver);
            }
            final X509Certificate rawCertificate = getRawCertificate(resolveInput);
            if (rawCertificate != null) {
                return rawCertificate.getPublicKey();
            }
            return null;
        }
        catch (final XMLSecurityException ex) {
            RetrievalMethodResolver.LOG.debug("XMLSecurityException", ex);
        }
        catch (final CertificateException ex2) {
            RetrievalMethodResolver.LOG.debug("CertificateException", ex2);
        }
        catch (final IOException ex3) {
            RetrievalMethodResolver.LOG.debug("IOException", ex3);
        }
        catch (final ParserConfigurationException ex4) {
            RetrievalMethodResolver.LOG.debug("ParserConfigurationException", ex4);
        }
        catch (final SAXException ex5) {
            RetrievalMethodResolver.LOG.debug("SAXException", ex5);
        }
        return null;
    }
    
    @Override
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) {
        if (!XMLUtils.elementIsInSignatureSpace(element, "RetrievalMethod")) {
            return null;
        }
        try {
            final RetrievalMethod retrievalMethod = new RetrievalMethod(element, s);
            final String type = retrievalMethod.getType();
            final XMLSignatureInput resolveInput = resolveInput(retrievalMethod, s, this.secureValidation);
            if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(type)) {
                return getRawCertificate(resolveInput);
            }
            final Element obtainReferenceElement = obtainReferenceElement(resolveInput, this.secureValidation);
            if (XMLUtils.elementIsInSignatureSpace(obtainReferenceElement, "RetrievalMethod")) {
                if (this.secureValidation) {
                    if (RetrievalMethodResolver.LOG.isDebugEnabled()) {
                        RetrievalMethodResolver.LOG.debug("Error: It is forbidden to have one RetrievalMethod point to another with secure validation");
                    }
                    return null;
                }
                if (obtainReferenceElement(resolveInput(new RetrievalMethod(obtainReferenceElement, s), s, this.secureValidation), this.secureValidation) == element) {
                    RetrievalMethodResolver.LOG.debug("Error: Can't have RetrievalMethods pointing to each other");
                    return null;
                }
            }
            return resolveCertificate(obtainReferenceElement, s, storageResolver);
        }
        catch (final XMLSecurityException ex) {
            RetrievalMethodResolver.LOG.debug("XMLSecurityException", ex);
        }
        catch (final CertificateException ex2) {
            RetrievalMethodResolver.LOG.debug("CertificateException", ex2);
        }
        catch (final IOException ex3) {
            RetrievalMethodResolver.LOG.debug("IOException", ex3);
        }
        catch (final ParserConfigurationException ex4) {
            RetrievalMethodResolver.LOG.debug("ParserConfigurationException", ex4);
        }
        catch (final SAXException ex5) {
            RetrievalMethodResolver.LOG.debug("SAXException", ex5);
        }
        return null;
    }
    
    private static X509Certificate resolveCertificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        if (RetrievalMethodResolver.LOG.isDebugEnabled()) {
            RetrievalMethodResolver.LOG.debug("Now we have a {" + element.getNamespaceURI() + "}" + element.getLocalName() + " Element");
        }
        if (element != null) {
            return KeyResolver.getX509Certificate(element, s, storageResolver);
        }
        return null;
    }
    
    private static PublicKey resolveKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        if (RetrievalMethodResolver.LOG.isDebugEnabled()) {
            RetrievalMethodResolver.LOG.debug("Now we have a {" + element.getNamespaceURI() + "}" + element.getLocalName() + " Element");
        }
        if (element != null) {
            return KeyResolver.getPublicKey(element, s, storageResolver);
        }
        return null;
    }
    
    private static Element obtainReferenceElement(final XMLSignatureInput xmlSignatureInput, final boolean b) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException {
        Element element;
        if (xmlSignatureInput.isElement()) {
            element = (Element)xmlSignatureInput.getSubNode();
        }
        else if (xmlSignatureInput.isNodeSet()) {
            element = getDocumentElement(xmlSignatureInput.getNodeSet());
        }
        else {
            final byte[] bytes = xmlSignatureInput.getBytes();
            element = KeyResolverSpi.getDocFromBytes(bytes, b);
            RetrievalMethodResolver.LOG.debug("we have to parse {} bytes", bytes.length);
        }
        return element;
    }
    
    private static X509Certificate getRawCertificate(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException, IOException, CertificateException {
        final byte[] bytes = xmlSignatureInput.getBytes();
        final CertificateFactory instance = CertificateFactory.getInstance("X.509");
        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            return (X509Certificate)instance.generateCertificate(byteArrayInputStream);
        }
    }
    
    private static XMLSignatureInput resolveInput(final RetrievalMethod retrievalMethod, final String s, final boolean b) throws XMLSecurityException {
        final Attr uriAttr = retrievalMethod.getURIAttr();
        final Transforms transforms = retrievalMethod.getTransforms();
        XMLSignatureInput xmlSignatureInput = ResourceResolver.getInstance(uriAttr, s, b).resolve(uriAttr, s, b);
        if (transforms != null) {
            RetrievalMethodResolver.LOG.debug("We have Transforms");
            xmlSignatureInput = transforms.performTransforms(xmlSignatureInput);
        }
        return xmlSignatureInput;
    }
    
    @Override
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    private static Element getDocumentElement(final Set<Node> set) {
        final Iterator<Node> iterator = set.iterator();
        Node node = null;
        while (iterator.hasNext()) {
            final Node node2 = iterator.next();
            if (node2 != null && 1 == node2.getNodeType()) {
                node = node2;
                break;
            }
        }
        final ArrayList list = new ArrayList();
        while (node != null) {
            list.add(node);
            final Node parentNode = node.getParentNode();
            if (parentNode == null) {
                break;
            }
            if (1 != parentNode.getNodeType()) {
                break;
            }
            node = parentNode;
        }
        final ListIterator listIterator = list.listIterator(list.size() - 1);
        while (listIterator.hasPrevious()) {
            final Element element = (Element)listIterator.previous();
            if (set.contains(element)) {
                return element;
            }
        }
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(RetrievalMethodResolver.class);
    }
}
