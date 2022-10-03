package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.slf4j.internal.LoggerFactory;
import com.sun.org.apache.xml.internal.security.utils.RFC2253Parser;
import java.security.cert.X509Certificate;
import java.math.BigInteger;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class XMLX509IssuerSerial extends SignatureElementProxy implements XMLX509DataContent
{
    private static final Logger LOG;
    
    public XMLX509IssuerSerial(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public XMLX509IssuerSerial(final Document document, final String s, final BigInteger bigInteger) {
        super(document);
        this.addReturnToSelf();
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
        this(document, x509Certificate.getIssuerX500Principal().getName(), x509Certificate.getSerialNumber());
    }
    
    public BigInteger getSerialNumber() {
        final String textFromChildElement = this.getTextFromChildElement("X509SerialNumber", "http://www.w3.org/2000/09/xmldsig#");
        XMLX509IssuerSerial.LOG.debug("X509SerialNumber text: {}", textFromChildElement);
        return new BigInteger(textFromChildElement);
    }
    
    public int getSerialNumberInteger() {
        return this.getSerialNumber().intValue();
    }
    
    public String getIssuerName() {
        return RFC2253Parser.normalize(this.getTextFromChildElement("X509IssuerName", "http://www.w3.org/2000/09/xmldsig#"));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof XMLX509IssuerSerial)) {
            return false;
        }
        final XMLX509IssuerSerial xmlx509IssuerSerial = (XMLX509IssuerSerial)o;
        return this.getSerialNumber().equals(xmlx509IssuerSerial.getSerialNumber()) && this.getIssuerName().equals(xmlx509IssuerSerial.getIssuerName());
    }
    
    @Override
    public int hashCode() {
        return 31 * (31 * 17 + this.getSerialNumber().hashCode()) + this.getIssuerName().hashCode();
    }
    
    @Override
    public String getBaseLocalName() {
        return "X509IssuerSerial";
    }
    
    static {
        LOG = LoggerFactory.getLogger(XMLX509IssuerSerial.class);
    }
}
