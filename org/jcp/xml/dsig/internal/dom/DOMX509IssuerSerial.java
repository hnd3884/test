package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import org.w3c.dom.Document;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;

public final class DOMX509IssuerSerial extends DOMStructure implements X509IssuerSerial
{
    private final String issuerName;
    private final BigInteger serialNumber;
    
    public DOMX509IssuerSerial(final String issuerName, final BigInteger serialNumber) {
        if (issuerName == null) {
            throw new NullPointerException("issuerName cannot be null");
        }
        if (serialNumber == null) {
            throw new NullPointerException("serialNumber cannot be null");
        }
        new X500Principal(issuerName);
        this.issuerName = issuerName;
        this.serialNumber = serialNumber;
    }
    
    public DOMX509IssuerSerial(final Element element) {
        final Element firstChildElement = DOMUtils.getFirstChildElement(element);
        final Element nextSiblingElement = DOMUtils.getNextSiblingElement(firstChildElement);
        this.issuerName = firstChildElement.getFirstChild().getNodeValue();
        this.serialNumber = new BigInteger(nextSiblingElement.getFirstChild().getNodeValue());
    }
    
    public String getIssuerName() {
        return this.issuerName;
    }
    
    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Document ownerDocument = DOMUtils.getOwnerDocument(node);
        final Element element = DOMUtils.createElement(ownerDocument, "X509IssuerSerial", "http://www.w3.org/2000/09/xmldsig#", s);
        final Element element2 = DOMUtils.createElement(ownerDocument, "X509IssuerName", "http://www.w3.org/2000/09/xmldsig#", s);
        final Element element3 = DOMUtils.createElement(ownerDocument, "X509SerialNumber", "http://www.w3.org/2000/09/xmldsig#", s);
        element2.appendChild(ownerDocument.createTextNode(this.issuerName));
        element3.appendChild(ownerDocument.createTextNode(this.serialNumber.toString()));
        element.appendChild(element2);
        element.appendChild(element3);
        node.appendChild(element);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof X509IssuerSerial)) {
            return false;
        }
        final X509IssuerSerial x509IssuerSerial = (X509IssuerSerial)o;
        return this.issuerName.equals(x509IssuerSerial.getIssuerName()) && this.serialNumber.equals(x509IssuerSerial.getSerialNumber());
    }
    
    public int hashCode() {
        return 52;
    }
}
