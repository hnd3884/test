package org.apache.xml.security.keys.keyresolver.implementations;

import org.apache.commons.logging.LogFactory;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.ArrayList;
import org.w3c.dom.Node;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xml.security.transforms.Transforms;
import org.w3c.dom.Attr;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.c14n.CanonicalizationException;
import java.security.cert.X509Certificate;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.cert.CertificateException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.RetrievalMethod;
import org.apache.xml.security.utils.XMLUtils;
import java.security.PublicKey;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;

public class RetrievalMethodResolver extends KeyResolverSpi
{
    static Log log;
    
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) {
        if (!XMLUtils.elementIsInSignatureSpace(element, "RetrievalMethod")) {
            return null;
        }
        try {
            final RetrievalMethod retrievalMethod = new RetrievalMethod(element, s);
            final String type = retrievalMethod.getType();
            final XMLSignatureInput resolveInput = resolveInput(retrievalMethod, s);
            if (!"http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(type)) {
                return resolveKey(obtainRefrenceElement(resolveInput), s, storageResolver);
            }
            final X509Certificate rawCertificate = getRawCertificate(resolveInput);
            if (rawCertificate != null) {
                return rawCertificate.getPublicKey();
            }
            return null;
        }
        catch (final XMLSecurityException ex) {
            RetrievalMethodResolver.log.debug((Object)"XMLSecurityException", (Throwable)ex);
        }
        catch (final CertificateException ex2) {
            RetrievalMethodResolver.log.debug((Object)"CertificateException", (Throwable)ex2);
        }
        catch (final IOException ex3) {
            RetrievalMethodResolver.log.debug((Object)"IOException", (Throwable)ex3);
        }
        catch (final ParserConfigurationException ex4) {
            RetrievalMethodResolver.log.debug((Object)"ParserConfigurationException", (Throwable)ex4);
        }
        catch (final SAXException ex5) {
            RetrievalMethodResolver.log.debug((Object)"SAXException", (Throwable)ex5);
        }
        return null;
    }
    
    private static Element obtainRefrenceElement(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException {
        Element element;
        if (xmlSignatureInput.isElement()) {
            element = (Element)xmlSignatureInput.getSubNode();
        }
        else if (xmlSignatureInput.isNodeSet()) {
            element = getDocumentElement(xmlSignatureInput.getNodeSet());
        }
        else {
            final byte[] bytes = xmlSignatureInput.getBytes();
            element = getDocFromBytes(bytes);
            if (RetrievalMethodResolver.log.isDebugEnabled()) {
                RetrievalMethodResolver.log.debug((Object)("we have to parse " + bytes.length + " bytes"));
            }
        }
        return element;
    }
    
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) {
        if (!XMLUtils.elementIsInSignatureSpace(element, "RetrievalMethod")) {
            return null;
        }
        try {
            final RetrievalMethod retrievalMethod = new RetrievalMethod(element, s);
            final String type = retrievalMethod.getType();
            final XMLSignatureInput resolveInput = resolveInput(retrievalMethod, s);
            if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(type)) {
                return getRawCertificate(resolveInput);
            }
            return resolveCertificate(obtainRefrenceElement(resolveInput), s, storageResolver);
        }
        catch (final XMLSecurityException ex) {
            RetrievalMethodResolver.log.debug((Object)"XMLSecurityException", (Throwable)ex);
        }
        catch (final CertificateException ex2) {
            RetrievalMethodResolver.log.debug((Object)"CertificateException", (Throwable)ex2);
        }
        catch (final IOException ex3) {
            RetrievalMethodResolver.log.debug((Object)"IOException", (Throwable)ex3);
        }
        catch (final ParserConfigurationException ex4) {
            RetrievalMethodResolver.log.debug((Object)"ParserConfigurationException", (Throwable)ex4);
        }
        catch (final SAXException ex5) {
            RetrievalMethodResolver.log.debug((Object)"SAXException", (Throwable)ex5);
        }
        return null;
    }
    
    private static X509Certificate resolveCertificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        if (RetrievalMethodResolver.log.isDebugEnabled()) {
            RetrievalMethodResolver.log.debug((Object)("Now we have a {" + element.getNamespaceURI() + "}" + element.getLocalName() + " Element"));
        }
        if (element != null) {
            return KeyResolver.getX509Certificate(element, s, storageResolver);
        }
        return null;
    }
    
    private static PublicKey resolveKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        if (RetrievalMethodResolver.log.isDebugEnabled()) {
            RetrievalMethodResolver.log.debug((Object)("Now we have a {" + element.getNamespaceURI() + "}" + element.getLocalName() + " Element"));
        }
        if (element != null) {
            return KeyResolver.getPublicKey(element, s, storageResolver);
        }
        return null;
    }
    
    private static X509Certificate getRawCertificate(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException, IOException, CertificateException {
        return (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(xmlSignatureInput.getBytes()));
    }
    
    private static XMLSignatureInput resolveInput(final RetrievalMethod retrievalMethod, final String s) throws XMLSecurityException {
        final Attr uriAttr = retrievalMethod.getURIAttr();
        final Transforms transforms = retrievalMethod.getTransforms();
        final ResourceResolver instance = ResourceResolver.getInstance(uriAttr, s);
        if (instance != null) {
            XMLSignatureInput xmlSignatureInput = instance.resolve(uriAttr, s);
            if (transforms != null) {
                RetrievalMethodResolver.log.debug((Object)"We have Transforms");
                xmlSignatureInput = transforms.performTransforms(xmlSignatureInput);
            }
            return xmlSignatureInput;
        }
        return null;
    }
    
    static Element getDocFromBytes(final byte[] array) throws KeyResolverException {
        try {
            final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
            instance.setNamespaceAware(true);
            return instance.newDocumentBuilder().parse(new ByteArrayInputStream(array)).getDocumentElement();
        }
        catch (final SAXException ex) {
            throw new KeyResolverException("empty", ex);
        }
        catch (final IOException ex2) {
            throw new KeyResolverException("empty", ex2);
        }
        catch (final ParserConfigurationException ex3) {
            throw new KeyResolverException("empty", ex3);
        }
    }
    
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    static Element getDocumentElement(final Set set) {
        final Iterator iterator = set.iterator();
        Node node = null;
        while (iterator.hasNext()) {
            final Node node2 = (Node)iterator.next();
            if (node2 instanceof Element) {
                node = node2;
                break;
            }
        }
        final ArrayList list = new ArrayList(10);
        do {
            list.add(node);
            final Node parentNode = node.getParentNode();
            if (!(parentNode instanceof Element)) {
                break;
            }
            node = parentNode;
        } while (node != null);
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
        RetrievalMethodResolver.log = LogFactory.getLog(RetrievalMethodResolver.class.getName());
    }
}
