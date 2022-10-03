package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerValue;

public class CertificateVersion implements CertAttrSet<String>
{
    public static final int V1 = 0;
    public static final int V2 = 1;
    public static final int V3 = 2;
    public static final String IDENT = "x509.info.version";
    public static final String NAME = "version";
    public static final String VERSION = "number";
    int version;
    
    private int getVersion() {
        return this.version;
    }
    
    private void construct(DerValue derValue) throws IOException {
        if (derValue.isConstructed() && derValue.isContextSpecific()) {
            derValue = derValue.data.getDerValue();
            this.version = derValue.getInteger();
            if (derValue.data.available() != 0) {
                throw new IOException("X.509 version, bad format");
            }
        }
    }
    
    public CertificateVersion() {
        this.version = 0;
        this.version = 0;
    }
    
    public CertificateVersion(final int version) throws IOException {
        this.version = 0;
        if (version == 0 || version == 1 || version == 2) {
            this.version = version;
            return;
        }
        throw new IOException("X.509 Certificate version " + version + " not supported.\n");
    }
    
    public CertificateVersion(final DerInputStream derInputStream) throws IOException {
        this.version = 0;
        this.version = 0;
        this.construct(derInputStream.getDerValue());
    }
    
    public CertificateVersion(final InputStream inputStream) throws IOException {
        this.version = 0;
        this.version = 0;
        this.construct(new DerValue(inputStream));
    }
    
    public CertificateVersion(final DerValue derValue) throws IOException {
        this.version = 0;
        this.version = 0;
        this.construct(derValue);
    }
    
    @Override
    public String toString() {
        return "Version: V" + (this.version + 1);
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        if (this.version == 0) {
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(this.version);
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream);
        outputStream.write(derOutputStream2.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof Integer)) {
            throw new IOException("Attribute must be of type Integer.");
        }
        if (s.equalsIgnoreCase("number")) {
            this.version = (int)o;
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet: CertificateVersion.");
    }
    
    @Override
    public Integer get(final String s) throws IOException {
        if (s.equalsIgnoreCase("number")) {
            return new Integer(this.getVersion());
        }
        throw new IOException("Attribute name not recognized by CertAttrSet: CertificateVersion.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("number")) {
            this.version = 0;
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet: CertificateVersion.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("number");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "version";
    }
    
    public int compare(final int n) {
        return this.version - n;
    }
}
