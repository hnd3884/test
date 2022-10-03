package org.apache.xml.security.keys.content.x509;

import org.w3c.dom.Document;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.SignatureElementProxy;

public class XMLX509CRL extends SignatureElementProxy implements XMLX509DataContent
{
    public XMLX509CRL(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public XMLX509CRL(final Document document, final byte[] array) {
        super(document);
        this.addBase64Text(array);
    }
    
    public byte[] getCRLBytes() throws XMLSecurityException {
        return this.getBytesFromTextChild();
    }
    
    public String getBaseLocalName() {
        return "X509CRL";
    }
}
