package org.apache.xml.security.keys.content;

import org.apache.commons.logging.LogFactory;
import java.security.cert.X509Certificate;
import java.math.BigInteger;
import org.w3c.dom.Node;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509CRL;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.SignatureElementProxy;

public class X509Data extends SignatureElementProxy implements KeyInfoContent
{
    static Log log;
    
    public X509Data(final Document document) {
        super(document);
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public X509Data(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        boolean b = true;
        Node node = super._constructionElement.getFirstChild();
        while (node != null) {
            if (node.getNodeType() != 1) {
                node = node.getNextSibling();
            }
            else {
                b = false;
                final Element element2 = (Element)node;
                node = node.getNextSibling();
                final String localName = element2.getLocalName();
                if (element2.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
                    if (localName.equals("X509IssuerSerial")) {
                        this.add(new XMLX509IssuerSerial(element2, s));
                    }
                    else if (localName.equals("X509SKI")) {
                        this.add(new XMLX509SKI(element2, s));
                    }
                    else if (localName.equals("X509SubjectName")) {
                        this.add(new XMLX509SubjectName(element2, s));
                    }
                    else if (localName.equals("X509Certificate")) {
                        this.add(new XMLX509Certificate(element2, s));
                    }
                    else if (localName.equals("X509CRL")) {
                        this.add(new XMLX509CRL(element2, s));
                    }
                    else {
                        X509Data.log.warn((Object)("Found a " + element2.getTagName() + " element in " + "X509Data"));
                        this.addUnknownElement(element2);
                    }
                }
                else {
                    X509Data.log.warn((Object)("Found a " + element2.getTagName() + " element in " + "X509Data"));
                    this.addUnknownElement(element2);
                }
            }
        }
        if (b) {
            throw new XMLSecurityException("xml.WrongContent", new Object[] { "Elements", "X509Data" });
        }
    }
    
    public void addIssuerSerial(final String s, final BigInteger bigInteger) {
        this.add(new XMLX509IssuerSerial(super._doc, s, bigInteger));
    }
    
    public void addIssuerSerial(final String s, final String s2) {
        this.add(new XMLX509IssuerSerial(super._doc, s, s2));
    }
    
    public void addIssuerSerial(final String s, final int n) {
        this.add(new XMLX509IssuerSerial(super._doc, s, n));
    }
    
    public void add(final XMLX509IssuerSerial xmlx509IssuerSerial) {
        if (super._state == 0) {
            super._constructionElement.appendChild(xmlx509IssuerSerial.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void addSKI(final byte[] array) {
        this.add(new XMLX509SKI(super._doc, array));
    }
    
    public void addSKI(final X509Certificate x509Certificate) throws XMLSecurityException {
        this.add(new XMLX509SKI(super._doc, x509Certificate));
    }
    
    public void add(final XMLX509SKI xmlx509SKI) {
        if (super._state == 0) {
            super._constructionElement.appendChild(xmlx509SKI.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void addSubjectName(final String s) {
        this.add(new XMLX509SubjectName(super._doc, s));
    }
    
    public void addSubjectName(final X509Certificate x509Certificate) {
        this.add(new XMLX509SubjectName(super._doc, x509Certificate));
    }
    
    public void add(final XMLX509SubjectName xmlx509SubjectName) {
        if (super._state == 0) {
            super._constructionElement.appendChild(xmlx509SubjectName.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void addCertificate(final X509Certificate x509Certificate) throws XMLSecurityException {
        this.add(new XMLX509Certificate(super._doc, x509Certificate));
    }
    
    public void addCertificate(final byte[] array) {
        this.add(new XMLX509Certificate(super._doc, array));
    }
    
    public void add(final XMLX509Certificate xmlx509Certificate) {
        if (super._state == 0) {
            super._constructionElement.appendChild(xmlx509Certificate.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void addCRL(final byte[] array) {
        this.add(new XMLX509CRL(super._doc, array));
    }
    
    public void add(final XMLX509CRL xmlx509CRL) {
        if (super._state == 0) {
            super._constructionElement.appendChild(xmlx509CRL.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void addUnknownElement(final Element element) {
        if (super._state == 0) {
            super._constructionElement.appendChild(element);
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public int lengthIssuerSerial() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "X509IssuerSerial");
    }
    
    public int lengthSKI() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "X509SKI");
    }
    
    public int lengthSubjectName() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "X509SubjectName");
    }
    
    public int lengthCertificate() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");
    }
    
    public int lengthCRL() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "X509CRL");
    }
    
    public int lengthUnknownElement() {
        int n = 0;
        for (Node node = super._constructionElement.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 1 && !node.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
                ++n;
            }
        }
        return n;
    }
    
    public XMLX509IssuerSerial itemIssuerSerial(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "X509IssuerSerial", n);
        if (selectDsNode != null) {
            return new XMLX509IssuerSerial(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public XMLX509SKI itemSKI(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "X509SKI", n);
        if (selectDsNode != null) {
            return new XMLX509SKI(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public XMLX509SubjectName itemSubjectName(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "X509SubjectName", n);
        if (selectDsNode != null) {
            return new XMLX509SubjectName(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public XMLX509Certificate itemCertificate(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "X509Certificate", n);
        if (selectDsNode != null) {
            return new XMLX509Certificate(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public XMLX509CRL itemCRL(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "X509CRL", n);
        if (selectDsNode != null) {
            return new XMLX509CRL(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public Element itemUnknownElement(final int n) {
        X509Data.log.debug((Object)("itemUnknownElement not implemented:" + n));
        return null;
    }
    
    public boolean containsIssuerSerial() {
        return this.lengthIssuerSerial() > 0;
    }
    
    public boolean containsSKI() {
        return this.lengthSKI() > 0;
    }
    
    public boolean containsSubjectName() {
        return this.lengthSubjectName() > 0;
    }
    
    public boolean containsCertificate() {
        return this.lengthCertificate() > 0;
    }
    
    public boolean containsCRL() {
        return this.lengthCRL() > 0;
    }
    
    public boolean containsUnknownElement() {
        return this.lengthUnknownElement() > 0;
    }
    
    public String getBaseLocalName() {
        return "X509Data";
    }
    
    static {
        X509Data.log = LogFactory.getLog(X509Data.class.getName());
    }
}
