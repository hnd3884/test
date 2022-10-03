package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import java.util.Iterator;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerOutputStream;

public class SubjectAlternativeNameExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.SubjectAlternativeName";
    public static final String NAME = "SubjectAlternativeName";
    public static final String SUBJECT_NAME = "subject_name";
    GeneralNames names;
    
    private void encodeThis() throws IOException {
        if (this.names == null || this.names.isEmpty()) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        this.names.encode(derOutputStream);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public SubjectAlternativeNameExtension(final GeneralNames generalNames) throws IOException {
        this(Boolean.FALSE, generalNames);
    }
    
    public SubjectAlternativeNameExtension(final Boolean b, final GeneralNames names) throws IOException {
        this.names = null;
        this.names = names;
        this.extensionId = PKIXExtensions.SubjectAlternativeName_Id;
        this.critical = b;
        this.encodeThis();
    }
    
    public SubjectAlternativeNameExtension() {
        this.names = null;
        this.extensionId = PKIXExtensions.SubjectAlternativeName_Id;
        this.critical = false;
        this.names = new GeneralNames();
    }
    
    public SubjectAlternativeNameExtension(final Boolean b, final Object o) throws IOException {
        this.names = null;
        this.extensionId = PKIXExtensions.SubjectAlternativeName_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.data == null) {
            this.names = new GeneralNames();
            return;
        }
        this.names = new GeneralNames(derValue);
    }
    
    @Override
    public String toString() {
        String s = super.toString() + "SubjectAlternativeName [\n";
        if (this.names == null) {
            s += "  null\n";
        }
        else {
            final Iterator<GeneralName> iterator = this.names.names().iterator();
            while (iterator.hasNext()) {
                s = s + "  " + iterator.next() + "\n";
            }
        }
        return s + "]\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.SubjectAlternativeName_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!s.equalsIgnoreCase("subject_name")) {
            throw new IOException("Attribute name not recognized by CertAttrSet:SubjectAlternativeName.");
        }
        if (!(o instanceof GeneralNames)) {
            throw new IOException("Attribute value should be of type GeneralNames.");
        }
        this.names = (GeneralNames)o;
        this.encodeThis();
    }
    
    @Override
    public GeneralNames get(final String s) throws IOException {
        if (s.equalsIgnoreCase("subject_name")) {
            return this.names;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:SubjectAlternativeName.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("subject_name")) {
            this.names = null;
            this.encodeThis();
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:SubjectAlternativeName.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("subject_name");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "SubjectAlternativeName";
    }
}
