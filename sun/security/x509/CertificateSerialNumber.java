package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.InputStream;
import java.io.IOException;
import sun.security.util.DerInputStream;
import java.math.BigInteger;

public class CertificateSerialNumber implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.serialNumber";
    public static final String NAME = "serialNumber";
    public static final String NUMBER = "number";
    private SerialNumber serial;
    
    public CertificateSerialNumber(final BigInteger bigInteger) {
        this.serial = new SerialNumber(bigInteger);
    }
    
    public CertificateSerialNumber(final int n) {
        this.serial = new SerialNumber(n);
    }
    
    public CertificateSerialNumber(final DerInputStream derInputStream) throws IOException {
        this.serial = new SerialNumber(derInputStream);
    }
    
    public CertificateSerialNumber(final InputStream inputStream) throws IOException {
        this.serial = new SerialNumber(inputStream);
    }
    
    public CertificateSerialNumber(final DerValue derValue) throws IOException {
        this.serial = new SerialNumber(derValue);
    }
    
    @Override
    public String toString() {
        if (this.serial == null) {
            return "";
        }
        return this.serial.toString();
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        this.serial.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof SerialNumber)) {
            throw new IOException("Attribute must be of type SerialNumber.");
        }
        if (s.equalsIgnoreCase("number")) {
            this.serial = (SerialNumber)o;
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSerialNumber.");
    }
    
    @Override
    public SerialNumber get(final String s) throws IOException {
        if (s.equalsIgnoreCase("number")) {
            return this.serial;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSerialNumber.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("number")) {
            this.serial = null;
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSerialNumber.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("number");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "serialNumber";
    }
}
