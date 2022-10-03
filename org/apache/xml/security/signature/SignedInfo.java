package org.apache.xml.security.signature;

import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.io.OutputStream;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xml.security.c14n.Canonicalizer;
import org.w3c.dom.Node;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.xml.security.algorithms.SignatureAlgorithm;

public class SignedInfo extends Manifest
{
    private SignatureAlgorithm _signatureAlgorithm;
    private byte[] _c14nizedBytes;
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
        this._signatureAlgorithm = null;
        this._c14nizedBytes = null;
        (this.c14nMethod = XMLUtils.createElementInSignatureSpace(super._doc, "CanonicalizationMethod")).setAttributeNS(null, "Algorithm", s2);
        super._constructionElement.appendChild(this.c14nMethod);
        XMLUtils.addReturnToElement(super._constructionElement);
        if (n > 0) {
            this._signatureAlgorithm = new SignatureAlgorithm(super._doc, s, n);
        }
        else {
            this._signatureAlgorithm = new SignatureAlgorithm(super._doc, s);
        }
        this.signatureMethod = this._signatureAlgorithm.getElement();
        super._constructionElement.appendChild(this.signatureMethod);
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public SignedInfo(final Document document, final Element element, final Element c14nMethod) throws XMLSecurityException {
        super(document);
        this._signatureAlgorithm = null;
        this._c14nizedBytes = null;
        this.c14nMethod = c14nMethod;
        super._constructionElement.appendChild(this.c14nMethod);
        XMLUtils.addReturnToElement(super._constructionElement);
        this._signatureAlgorithm = new SignatureAlgorithm(element, null);
        this.signatureMethod = this._signatureAlgorithm.getElement();
        super._constructionElement.appendChild(this.signatureMethod);
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public SignedInfo(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        this._signatureAlgorithm = null;
        this._c14nizedBytes = null;
        this.c14nMethod = XMLUtils.getNextElement(element.getFirstChild());
        final String canonicalizationMethodURI = this.getCanonicalizationMethodURI();
        if (!canonicalizationMethodURI.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315") && !canonicalizationMethodURI.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments") && !canonicalizationMethodURI.equals("http://www.w3.org/2001/10/xml-exc-c14n#") && !canonicalizationMethodURI.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) {
            try {
                this._c14nizedBytes = Canonicalizer.getInstance(this.getCanonicalizationMethodURI()).canonicalizeSubtree(super._constructionElement);
                final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
                instance.setNamespaceAware(true);
                final Node importNode = super._doc.importNode(instance.newDocumentBuilder().parse(new ByteArrayInputStream(this._c14nizedBytes)).getDocumentElement(), true);
                super._constructionElement.getParentNode().replaceChild(importNode, super._constructionElement);
                super._constructionElement = (Element)importNode;
            }
            catch (final ParserConfigurationException ex) {
                throw new XMLSecurityException("empty", ex);
            }
            catch (final IOException ex2) {
                throw new XMLSecurityException("empty", ex2);
            }
            catch (final SAXException ex3) {
                throw new XMLSecurityException("empty", ex3);
            }
        }
        this.signatureMethod = XMLUtils.getNextElement(this.c14nMethod.getNextSibling());
        this._signatureAlgorithm = new SignatureAlgorithm(this.signatureMethod, this.getBaseURI());
    }
    
    public boolean verify() throws MissingResourceFailureException, XMLSecurityException {
        return super.verifyReferences(false);
    }
    
    public boolean verify(final boolean b) throws MissingResourceFailureException, XMLSecurityException {
        return super.verifyReferences(b);
    }
    
    public byte[] getCanonicalizedOctetStream() throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
        if (this._c14nizedBytes == null) {
            this._c14nizedBytes = Canonicalizer.getInstance(this.getCanonicalizationMethodURI()).canonicalizeSubtree(super._constructionElement);
        }
        final byte[] array = new byte[this._c14nizedBytes.length];
        System.arraycopy(this._c14nizedBytes, 0, array, 0, array.length);
        return array;
    }
    
    public void signInOctectStream(final OutputStream writer) throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
        if (this._c14nizedBytes == null) {
            final Canonicalizer instance = Canonicalizer.getInstance(this.getCanonicalizationMethodURI());
            instance.setWriter(writer);
            final String inclusiveNamespaces = this.getInclusiveNamespaces();
            if (inclusiveNamespaces == null) {
                instance.canonicalizeSubtree(super._constructionElement);
            }
            else {
                instance.canonicalizeSubtree(super._constructionElement, inclusiveNamespaces);
            }
        }
        else {
            try {
                writer.write(this._c14nizedBytes);
            }
            catch (final IOException ex) {
                throw new RuntimeException("" + ex);
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
        return new SecretKeySpec(array, this._signatureAlgorithm.getJCEAlgorithmString());
    }
    
    protected SignatureAlgorithm getSignatureAlgorithm() {
        return this._signatureAlgorithm;
    }
    
    public String getBaseLocalName() {
        return "SignedInfo";
    }
    
    public String getInclusiveNamespaces() {
        final String attributeNS = this.c14nMethod.getAttributeNS(null, "Algorithm");
        if (!attributeNS.equals("http://www.w3.org/2001/10/xml-exc-c14n#") && !attributeNS.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) {
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
