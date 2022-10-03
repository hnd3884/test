package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.utils.RFC2253Parser;
import java.security.cert.X509Certificate;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

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
        this(document, x509Certificate.getSubjectX500Principal().getName());
    }
    
    public String getSubjectName() {
        return RFC2253Parser.normalize(this.getTextFromTextChild());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof XMLX509SubjectName && this.getSubjectName().equals(((XMLX509SubjectName)o).getSubjectName());
    }
    
    @Override
    public int hashCode() {
        return 31 * 17 + this.getSubjectName().hashCode();
    }
    
    @Override
    public String getBaseLocalName() {
        return "X509SubjectName";
    }
}
