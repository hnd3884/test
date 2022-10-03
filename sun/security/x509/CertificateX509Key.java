package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.InputStream;
import java.io.IOException;
import sun.security.util.DerInputStream;
import java.security.PublicKey;

public class CertificateX509Key implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.key";
    public static final String NAME = "key";
    public static final String KEY = "value";
    private PublicKey key;
    
    public CertificateX509Key(final PublicKey key) {
        this.key = key;
    }
    
    public CertificateX509Key(final DerInputStream derInputStream) throws IOException {
        this.key = X509Key.parse(derInputStream.getDerValue());
    }
    
    public CertificateX509Key(final InputStream inputStream) throws IOException {
        this.key = X509Key.parse(new DerValue(inputStream));
    }
    
    @Override
    public String toString() {
        if (this.key == null) {
            return "";
        }
        return this.key.toString();
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.write(this.key.getEncoded());
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (s.equalsIgnoreCase("value")) {
            this.key = (PublicKey)o;
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet: CertificateX509Key.");
    }
    
    @Override
    public PublicKey get(final String s) throws IOException {
        if (s.equalsIgnoreCase("value")) {
            return this.key;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet: CertificateX509Key.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("value")) {
            this.key = null;
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet: CertificateX509Key.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("value");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "key";
    }
}
