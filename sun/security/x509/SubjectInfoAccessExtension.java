package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Collections;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import sun.security.util.DerValue;
import java.io.IOException;
import java.util.List;

public class SubjectInfoAccessExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.SubjectInfoAccess";
    public static final String NAME = "SubjectInfoAccess";
    public static final String DESCRIPTIONS = "descriptions";
    private List<AccessDescription> accessDescriptions;
    
    public SubjectInfoAccessExtension(final List<AccessDescription> accessDescriptions) throws IOException {
        this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
        this.critical = false;
        this.accessDescriptions = accessDescriptions;
        this.encodeThis();
    }
    
    public SubjectInfoAccessExtension(final Boolean b, final Object o) throws IOException {
        this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
        this.critical = b;
        if (!(o instanceof byte[])) {
            throw new IOException("Illegal argument type");
        }
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for SubjectInfoAccessExtension.");
        }
        this.accessDescriptions = new ArrayList<AccessDescription>();
        while (derValue.data.available() != 0) {
            this.accessDescriptions.add(new AccessDescription(derValue.data.getDerValue()));
        }
    }
    
    public List<AccessDescription> getAccessDescriptions() {
        return this.accessDescriptions;
    }
    
    @Override
    public String getName() {
        return "SubjectInfoAccess";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!s.equalsIgnoreCase("descriptions")) {
            throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
        }
        if (!(o instanceof List)) {
            throw new IOException("Attribute value should be of type List.");
        }
        this.accessDescriptions = (List)o;
        this.encodeThis();
    }
    
    @Override
    public List<AccessDescription> get(final String s) throws IOException {
        if (s.equalsIgnoreCase("descriptions")) {
            return this.accessDescriptions;
        }
        throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("descriptions")) {
            this.accessDescriptions = Collections.emptyList();
            this.encodeThis();
            return;
        }
        throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("descriptions");
        return attributeNameEnumeration.elements();
    }
    
    private void encodeThis() throws IOException {
        if (this.accessDescriptions.isEmpty()) {
            this.extensionValue = null;
        }
        else {
            final DerOutputStream derOutputStream = new DerOutputStream();
            final Iterator<AccessDescription> iterator = this.accessDescriptions.iterator();
            while (iterator.hasNext()) {
                iterator.next().encode(derOutputStream);
            }
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            derOutputStream2.write((byte)48, derOutputStream);
            this.extensionValue = derOutputStream2.toByteArray();
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + "SubjectInfoAccess [\n  " + this.accessDescriptions + "\n]\n";
    }
}
