package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerOutputStream;

public class CertificateIssuerExtension extends Extension implements CertAttrSet<String>
{
    public static final String NAME = "CertificateIssuer";
    public static final String ISSUER = "issuer";
    private GeneralNames names;
    
    private void encodeThis() throws IOException {
        if (this.names == null || this.names.isEmpty()) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        this.names.encode(derOutputStream);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public CertificateIssuerExtension(final GeneralNames names) throws IOException {
        this.extensionId = PKIXExtensions.CertificateIssuer_Id;
        this.critical = true;
        this.names = names;
        this.encodeThis();
    }
    
    public CertificateIssuerExtension(final Boolean b, final Object o) throws IOException {
        this.extensionId = PKIXExtensions.CertificateIssuer_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        this.names = new GeneralNames(new DerValue(this.extensionValue));
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!s.equalsIgnoreCase("issuer")) {
            throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuer");
        }
        if (!(o instanceof GeneralNames)) {
            throw new IOException("Attribute value must be of type GeneralNames");
        }
        this.names = (GeneralNames)o;
        this.encodeThis();
    }
    
    @Override
    public GeneralNames get(final String s) throws IOException {
        if (s.equalsIgnoreCase("issuer")) {
            return this.names;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuer");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("issuer")) {
            this.names = null;
            this.encodeThis();
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuer");
    }
    
    @Override
    public String toString() {
        return super.toString() + "Certificate Issuer [\n" + String.valueOf(this.names) + "]\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.CertificateIssuer_Id;
            this.critical = true;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("issuer");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "CertificateIssuer";
    }
}
