package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.slf4j.internal.LoggerFactory;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Digest;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509CRL;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SubjectName;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import java.math.BigInteger;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class X509Data extends SignatureElementProxy implements KeyInfoContent
{
    private static final Logger LOG;
    
    public X509Data(final Document document) {
        super(document);
        this.addReturnToSelf();
    }
    
    public X509Data(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        Node node;
        for (node = this.getFirstChild(); node != null && node.getNodeType() != 1; node = node.getNextSibling()) {}
        if (node == null || node.getNodeType() != 1) {
            throw new XMLSecurityException("xml.WrongContent", new Object[] { "Elements", "X509Data" });
        }
    }
    
    public void addIssuerSerial(final String s, final BigInteger bigInteger) {
        this.add(new XMLX509IssuerSerial(this.getDocument(), s, bigInteger));
    }
    
    public void addIssuerSerial(final String s, final String s2) {
        this.add(new XMLX509IssuerSerial(this.getDocument(), s, s2));
    }
    
    public void addIssuerSerial(final String s, final int n) {
        this.add(new XMLX509IssuerSerial(this.getDocument(), s, n));
    }
    
    public void add(final XMLX509IssuerSerial xmlx509IssuerSerial) {
        this.appendSelf(xmlx509IssuerSerial);
        this.addReturnToSelf();
    }
    
    public void addSKI(final byte[] array) {
        this.add(new XMLX509SKI(this.getDocument(), array));
    }
    
    public void addSKI(final X509Certificate x509Certificate) throws XMLSecurityException {
        this.add(new XMLX509SKI(this.getDocument(), x509Certificate));
    }
    
    public void add(final XMLX509SKI xmlx509SKI) {
        this.appendSelf(xmlx509SKI);
        this.addReturnToSelf();
    }
    
    public void addSubjectName(final String s) {
        this.add(new XMLX509SubjectName(this.getDocument(), s));
    }
    
    public void addSubjectName(final X509Certificate x509Certificate) {
        this.add(new XMLX509SubjectName(this.getDocument(), x509Certificate));
    }
    
    public void add(final XMLX509SubjectName xmlx509SubjectName) {
        this.appendSelf(xmlx509SubjectName);
        this.addReturnToSelf();
    }
    
    public void addCertificate(final X509Certificate x509Certificate) throws XMLSecurityException {
        this.add(new XMLX509Certificate(this.getDocument(), x509Certificate));
    }
    
    public void addCertificate(final byte[] array) {
        this.add(new XMLX509Certificate(this.getDocument(), array));
    }
    
    public void add(final XMLX509Certificate xmlx509Certificate) {
        this.appendSelf(xmlx509Certificate);
        this.addReturnToSelf();
    }
    
    public void addCRL(final byte[] array) {
        this.add(new XMLX509CRL(this.getDocument(), array));
    }
    
    public void add(final XMLX509CRL xmlx509CRL) {
        this.appendSelf(xmlx509CRL);
        this.addReturnToSelf();
    }
    
    public void addDigest(final X509Certificate x509Certificate, final String s) throws XMLSecurityException {
        this.add(new XMLX509Digest(this.getDocument(), x509Certificate, s));
    }
    
    public void addDigest(final byte[] array, final String s) {
        this.add(new XMLX509Digest(this.getDocument(), array, s));
    }
    
    public void add(final XMLX509Digest xmlx509Digest) {
        this.appendSelf(xmlx509Digest);
        this.addReturnToSelf();
    }
    
    public void addUnknownElement(final Element element) {
        this.appendSelf(element);
        this.addReturnToSelf();
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
    
    public int lengthDigest() {
        return this.length("http://www.w3.org/2009/xmldsig11#", "X509Digest");
    }
    
    public int lengthUnknownElement() {
        int n = 0;
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 1 && !node.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
                ++n;
            }
        }
        return n;
    }
    
    public XMLX509IssuerSerial itemIssuerSerial(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "X509IssuerSerial", n);
        if (selectDsNode != null) {
            return new XMLX509IssuerSerial(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public XMLX509SKI itemSKI(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "X509SKI", n);
        if (selectDsNode != null) {
            return new XMLX509SKI(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public XMLX509SubjectName itemSubjectName(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "X509SubjectName", n);
        if (selectDsNode != null) {
            return new XMLX509SubjectName(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public XMLX509Certificate itemCertificate(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "X509Certificate", n);
        if (selectDsNode != null) {
            return new XMLX509Certificate(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public XMLX509CRL itemCRL(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "X509CRL", n);
        if (selectDsNode != null) {
            return new XMLX509CRL(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public XMLX509Digest itemDigest(final int n) throws XMLSecurityException {
        final Element selectDs11Node = XMLUtils.selectDs11Node(this.getFirstChild(), "X509Digest", n);
        if (selectDs11Node != null) {
            return new XMLX509Digest(selectDs11Node, this.baseURI);
        }
        return null;
    }
    
    public Element itemUnknownElement(final int n) {
        X509Data.LOG.debug("itemUnknownElement not implemented: {}", n);
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
    
    public boolean containsDigest() {
        return this.lengthDigest() > 0;
    }
    
    public boolean containsCRL() {
        return this.lengthCRL() > 0;
    }
    
    public boolean containsUnknownElement() {
        return this.lengthUnknownElement() > 0;
    }
    
    @Override
    public String getBaseLocalName() {
        return "X509Data";
    }
    
    static {
        LOG = LoggerFactory.getLogger(X509Data.class);
    }
}
