package org.apache.xml.security.keys.content.x509;

import org.apache.xml.security.utils.RFC2253Parser;
import java.security.cert.X509Certificate;
import org.w3c.dom.Document;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.SignatureElementProxy;

public class XMLX509SubjectName extends SignatureElementProxy implements XMLX509DataContent
{
    public XMLX509SubjectName(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public XMLX509SubjectName(final Document document, final String s) {
        super(document);
        this.addText(s);
    }
    
    public XMLX509SubjectName(final Document document, final X509Certificate x509Certificate) {
        this(document, RFC2253Parser.normalize(x509Certificate.getSubjectDN().getName()));
    }
    
    public String getSubjectName() {
        return RFC2253Parser.normalize(this.getTextFromTextChild());
    }
    
    public boolean equals(final Object o) {
        return o != null && this.getClass().getName().equals(o.getClass().getName()) && this.getSubjectName().equals(((XMLX509SubjectName)o).getSubjectName());
    }
    
    public int hashCode() {
        return 52;
    }
    
    public String getBaseLocalName() {
        return "X509SubjectName";
    }
}
