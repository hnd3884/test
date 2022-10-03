package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;

public class SignedInfo extends Manifest
{
    private SignatureAlgorithm signatureAlgorithm;
    private byte[] c14nizedBytes;
    private Element c14nMethod;
    private Element signatureMethod;
    
    public SignedInfo(final Document document) throws XMLSecurityException {
        this(document, "http://www.w3.org/2000/09/xmldsig#dsa-sha1", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
    }
    
    public SignedInfo(final Document document, final String s, final String s2) throws XMLSecurityException {
        this(document, s, 0, s2);
    }
    
    public SignedInfo(final Document document, final String s, final int n, final String s2) throws XMLSecurityException {
        super(document);
        (this.c14nMethod = XMLUtils.createElementInSignatureSpace(this.getDocument(), "CanonicalizationMethod")).setAttributeNS(null, "Algorithm", s2);
        this.appendSelf(this.c14nMethod);
        this.addReturnToSelf();
        if (n > 0) {
            this.signatureAlgorithm = new SignatureAlgorithm(this.getDocument(), s, n);
        }
        else {
            this.signatureAlgorithm = new SignatureAlgorithm(this.getDocument(), s);
        }
        this.appendSelf(this.signatureMethod = this.signatureAlgorithm.getElement());
        this.addReturnToSelf();
    }
    
    public SignedInfo(final Document document, final Element element, final Element c14nMethod) throws XMLSecurityException {
        super(document);
        this.appendSelf(this.c14nMethod = c14nMethod);
        this.addReturnToSelf();
        this.signatureAlgorithm = new SignatureAlgorithm(element, null);
        this.appendSelf(this.signatureMethod = this.signatureAlgorithm.getElement());
        this.addReturnToSelf();
    }
    
    public SignedInfo(final Element element, final String s) throws XMLSecurityException {
        this(element, s, true);
    }
    
    public SignedInfo(final Element element, final String s, final boolean b) throws XMLSecurityException {
        super(reparseSignedInfoElem(element, b), s, b);
        this.c14nMethod = XMLUtils.getNextElement(element.getFirstChild());
        this.signatureMethod = XMLUtils.getNextElement(this.c14nMethod.getNextSibling());
        this.signatureAlgorithm = new SignatureAlgorithm(this.signatureMethod, this.getBaseURI(), b);
    }
    
    private static Element reparseSignedInfoElem(final Element element, final boolean secureValidation) throws XMLSecurityException {
        final String attributeNS = XMLUtils.getNextElement(element.getFirstChild()).getAttributeNS(null, "Algorithm");
        if (!attributeNS.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315") && !attributeNS.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments") && !attributeNS.equals("http://www.w3.org/2001/10/xml-exc-c14n#") && !attributeNS.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments") && !attributeNS.equals("http://www.w3.org/2006/12/xml-c14n11") && !attributeNS.equals("http://www.w3.org/2006/12/xml-c14n11#WithComments")) {
            try {
                final Canonicalizer instance = Canonicalizer.getInstance(attributeNS);
                instance.setSecureValidation(secureValidation);
                final byte[] canonicalizeSubtree = instance.canonicalizeSubtree(element);
                XMLUtils.createDocumentBuilder(false, secureValidation);
                try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(canonicalizeSubtree)) {
                    final Node importNode = element.getOwnerDocument().importNode(XMLUtils.read(byteArrayInputStream, secureValidation).getDocumentElement(), true);
                    element.getParentNode().replaceChild(importNode, element);
                    return (Element)importNode;
                }
            }
            catch (final ParserConfigurationException ex) {
                throw new XMLSecurityException(ex);
            }
            catch (final IOException ex2) {
                throw new XMLSecurityException(ex2);
            }
            catch (final SAXException ex3) {
                throw new XMLSecurityException(ex3);
            }
        }
        return element;
    }
    
    public boolean verify() throws MissingResourceFailureException, XMLSecurityException {
        return super.verifyReferences(false);
    }
    
    public boolean verify(final boolean b) throws MissingResourceFailureException, XMLSecurityException {
        return super.verifyReferences(b);
    }
    
    public byte[] getCanonicalizedOctetStream() throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
        if (this.c14nizedBytes == null) {
            final Canonicalizer instance = Canonicalizer.getInstance(this.getCanonicalizationMethodURI());
            instance.setSecureValidation(this.isSecureValidation());
            final String inclusiveNamespaces = this.getInclusiveNamespaces();
            if (inclusiveNamespaces == null) {
                this.c14nizedBytes = instance.canonicalizeSubtree(this.getElement());
            }
            else {
                this.c14nizedBytes = instance.canonicalizeSubtree(this.getElement(), inclusiveNamespaces);
            }
        }
        return this.c14nizedBytes.clone();
    }
    
    public void signInOctetStream(final OutputStream writer) throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
        if (this.c14nizedBytes == null) {
            final Canonicalizer instance = Canonicalizer.getInstance(this.getCanonicalizationMethodURI());
            instance.setSecureValidation(this.isSecureValidation());
            instance.setWriter(writer);
            final String inclusiveNamespaces = this.getInclusiveNamespaces();
            if (inclusiveNamespaces == null) {
                instance.canonicalizeSubtree(this.getElement());
            }
            else {
                instance.canonicalizeSubtree(this.getElement(), inclusiveNamespaces);
            }
        }
        else {
            try {
                writer.write(this.c14nizedBytes);
            }
            catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    public String getCanonicalizationMethodURI() {
        return this.c14nMethod.getAttributeNS(null, "Algorithm");
    }
    
    public String getSignatureMethodURI() {
        final Element signatureMethodElement = this.getSignatureMethodElement();
        if (signatureMethodElement != null) {
            return signatureMethodElement.getAttributeNS(null, "Algorithm");
        }
        return null;
    }
    
    public Element getSignatureMethodElement() {
        return this.signatureMethod;
    }
    
    public SecretKey createSecretKey(final byte[] array) {
        return new SecretKeySpec(array, this.signatureAlgorithm.getJCEAlgorithmString());
    }
    
    public SignatureAlgorithm getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }
    
    @Override
    public String getBaseLocalName() {
        return "SignedInfo";
    }
    
    public String getInclusiveNamespaces() {
        final String canonicalizationMethodURI = this.getCanonicalizationMethodURI();
        if (!canonicalizationMethodURI.equals("http://www.w3.org/2001/10/xml-exc-c14n#") && !canonicalizationMethodURI.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) {
            return null;
        }
        final Element nextElement = XMLUtils.getNextElement(this.c14nMethod.getFirstChild());
        if (nextElement != null) {
            try {
                return new InclusiveNamespaces(nextElement, "http://www.w3.org/2001/10/xml-exc-c14n#").getInclusiveNamespaces();
            }
            catch (final XMLSecurityException ex) {
                return null;
            }
        }
        return null;
    }
}
