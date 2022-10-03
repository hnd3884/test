package sun.security.x509;

import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.InputStream;
import java.io.IOException;
import sun.security.util.DerInputStream;
import javax.security.auth.x500.X500Principal;

public class CertificateSubjectName implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.subject";
    public static final String NAME = "subject";
    public static final String DN_NAME = "dname";
    public static final String DN_PRINCIPAL = "x500principal";
    private X500Name dnName;
    private X500Principal dnPrincipal;
    
    public CertificateSubjectName(final X500Name dnName) {
        this.dnName = dnName;
    }
    
    public CertificateSubjectName(final DerInputStream derInputStream) throws IOException {
        this.dnName = new X500Name(derInputStream);
    }
    
    public CertificateSubjectName(final InputStream inputStream) throws IOException {
        this.dnName = new X500Name(new DerValue(inputStream));
    }
    
    @Override
    public String toString() {
        if (this.dnName == null) {
            return "";
        }
        return this.dnName.toString();
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        this.dnName.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof X500Name)) {
            throw new IOException("Attribute must be of type X500Name.");
        }
        if (s.equalsIgnoreCase("dname")) {
            this.dnName = (X500Name)o;
            this.dnPrincipal = null;
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSubjectName.");
    }
    
    @Override
    public Object get(final String s) throws IOException {
        if (s.equalsIgnoreCase("dname")) {
            return this.dnName;
        }
        if (s.equalsIgnoreCase("x500principal")) {
            if (this.dnPrincipal == null && this.dnName != null) {
                this.dnPrincipal = this.dnName.asX500Principal();
            }
            return this.dnPrincipal;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSubjectName.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("dname")) {
            this.dnName = null;
            this.dnPrincipal = null;
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSubjectName.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("dname");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "subject";
    }
}
