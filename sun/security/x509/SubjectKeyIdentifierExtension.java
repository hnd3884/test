package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerOutputStream;

public class SubjectKeyIdentifierExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.SubjectKeyIdentifier";
    public static final String NAME = "SubjectKeyIdentifier";
    public static final String KEY_ID = "key_id";
    private KeyIdentifier id;
    
    private void encodeThis() throws IOException {
        if (this.id == null) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        this.id.encode(derOutputStream);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public SubjectKeyIdentifierExtension(final byte[] array) throws IOException {
        this.id = null;
        this.id = new KeyIdentifier(array);
        this.extensionId = PKIXExtensions.SubjectKey_Id;
        this.critical = false;
        this.encodeThis();
    }
    
    public SubjectKeyIdentifierExtension(final Boolean b, final Object o) throws IOException {
        this.id = null;
        this.extensionId = PKIXExtensions.SubjectKey_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        this.id = new KeyIdentifier(new DerValue(this.extensionValue));
    }
    
    @Override
    public String toString() {
        return super.toString() + "SubjectKeyIdentifier [\n" + String.valueOf(this.id) + "]\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.SubjectKey_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!s.equalsIgnoreCase("key_id")) {
            throw new IOException("Attribute name not recognized by CertAttrSet:SubjectKeyIdentifierExtension.");
        }
        if (!(o instanceof KeyIdentifier)) {
            throw new IOException("Attribute value should be of type KeyIdentifier.");
        }
        this.id = (KeyIdentifier)o;
        this.encodeThis();
    }
    
    @Override
    public KeyIdentifier get(final String s) throws IOException {
        if (s.equalsIgnoreCase("key_id")) {
            return this.id;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:SubjectKeyIdentifierExtension.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("key_id")) {
            this.id = null;
            this.encodeThis();
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:SubjectKeyIdentifierExtension.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("key_id");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "SubjectKeyIdentifier";
    }
}
