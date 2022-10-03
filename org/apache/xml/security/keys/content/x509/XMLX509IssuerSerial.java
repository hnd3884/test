package org.apache.xml.security.keys.content.x509;

import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.utils.RFC2253Parser;
import java.security.cert.X509Certificate;
import org.apache.xml.security.utils.XMLUtils;
import java.math.BigInteger;
import org.w3c.dom.Document;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.SignatureElementProxy;

public class XMLX509IssuerSerial extends SignatureElementProxy implements XMLX509DataContent
{
    static Log log;
    
    public XMLX509IssuerSerial(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public XMLX509IssuerSerial(final Document document, final String s, final BigInteger bigInteger) {
        super(document);
        XMLUtils.addReturnToElement(super._constructionElement);
        this.addTextElement(s, "X509IssuerName");
        this.addTextElement(bigInteger.toString(), "X509SerialNumber");
    }
    
    public XMLX509IssuerSerial(final Document document, final String s, final String s2) {
        this(document, s, new BigInteger(s2));
    }
    
    public XMLX509IssuerSerial(final Document document, final String s, final int n) {
        this(document, s, new BigInteger(Integer.toString(n)));
    }
    
    public XMLX509IssuerSerial(final Document document, final X509Certificate x509Certificate) {
        this(document, RFC2253Parser.normalize(x509Certificate.getIssuerDN().getName()), x509Certificate.getSerialNumber());
    }
    
    public BigInteger getSerialNumber() {
        final String textFromChildElement = this.getTextFromChildElement("X509SerialNumber", "http://www.w3.org/2000/09/xmldsig#");
        if (XMLX509IssuerSerial.log.isDebugEnabled()) {
            XMLX509IssuerSerial.log.debug((Object)("X509SerialNumber text: " + textFromChildElement));
        }
        return new BigInteger(textFromChildElement);
    }
    
    public int getSerialNumberInteger() {
        return this.getSerialNumber().intValue();
    }
    
    public String getIssuerName() {
        return RFC2253Parser.normalize(this.getTextFromChildElement("X509IssuerName", "http://www.w3.org/2000/09/xmldsig#"));
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!this.getClass().getName().equals(o.getClass().getName())) {
            return false;
        }
        final XMLX509IssuerSerial xmlx509IssuerSerial = (XMLX509IssuerSerial)o;
        return this.getSerialNumber().equals(xmlx509IssuerSerial.getSerialNumber()) && this.getIssuerName().equals(xmlx509IssuerSerial.getIssuerName());
    }
    
    public int hashCode() {
        return 82;
    }
    
    public String getBaseLocalName() {
        return "X509IssuerSerial";
    }
    
    static {
        XMLX509IssuerSerial.log = LogFactory.getLog(XMLX509IssuerSerial.class.getName());
    }
}
