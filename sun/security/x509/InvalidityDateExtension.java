package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerOutputStream;
import java.util.Date;

public class InvalidityDateExtension extends Extension implements CertAttrSet<String>
{
    public static final String NAME = "InvalidityDate";
    public static final String DATE = "date";
    private Date date;
    
    private void encodeThis() throws IOException {
        if (this.date == null) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putGeneralizedTime(this.date);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public InvalidityDateExtension(final Date date) throws IOException {
        this(false, date);
    }
    
    public InvalidityDateExtension(final boolean critical, final Date date) throws IOException {
        this.extensionId = PKIXExtensions.InvalidityDate_Id;
        this.critical = critical;
        this.date = date;
        this.encodeThis();
    }
    
    public InvalidityDateExtension(final Boolean b, final Object o) throws IOException {
        this.extensionId = PKIXExtensions.InvalidityDate_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        this.date = new DerValue(this.extensionValue).getGeneralizedTime();
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof Date)) {
            throw new IOException("Attribute must be of type Date.");
        }
        if (s.equalsIgnoreCase("date")) {
            this.date = (Date)o;
            this.encodeThis();
            return;
        }
        throw new IOException("Name not supported by InvalidityDateExtension");
    }
    
    @Override
    public Date get(final String s) throws IOException {
        if (!s.equalsIgnoreCase("date")) {
            throw new IOException("Name not supported by InvalidityDateExtension");
        }
        if (this.date == null) {
            return null;
        }
        return new Date(this.date.getTime());
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("date")) {
            this.date = null;
            this.encodeThis();
            return;
        }
        throw new IOException("Name not supported by InvalidityDateExtension");
    }
    
    @Override
    public String toString() {
        return super.toString() + "    Invalidity Date: " + String.valueOf(this.date);
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.InvalidityDate_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("date");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "InvalidityDate";
    }
    
    public static InvalidityDateExtension toImpl(final java.security.cert.Extension extension) throws IOException {
        if (extension instanceof InvalidityDateExtension) {
            return (InvalidityDateExtension)extension;
        }
        return new InvalidityDateExtension(extension.isCritical(), extension.getValue());
    }
}
