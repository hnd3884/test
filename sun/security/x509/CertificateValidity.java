package sun.security.x509;

import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import java.util.Date;

public class CertificateValidity implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.validity";
    public static final String NAME = "validity";
    public static final String NOT_BEFORE = "notBefore";
    public static final String NOT_AFTER = "notAfter";
    static final long YR_2050 = 2524608000000L;
    private Date notBefore;
    private Date notAfter;
    
    private Date getNotBefore() {
        return new Date(this.notBefore.getTime());
    }
    
    private Date getNotAfter() {
        return new Date(this.notAfter.getTime());
    }
    
    private void construct(final DerValue derValue) throws IOException {
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoded CertificateValidity, starting sequence tag missing.");
        }
        if (derValue.data.available() == 0) {
            throw new IOException("No data encoded for CertificateValidity");
        }
        final DerValue[] sequence = new DerInputStream(derValue.toByteArray()).getSequence(2);
        if (sequence.length != 2) {
            throw new IOException("Invalid encoding for CertificateValidity");
        }
        if (sequence[0].tag == 23) {
            this.notBefore = derValue.data.getUTCTime();
        }
        else {
            if (sequence[0].tag != 24) {
                throw new IOException("Invalid encoding for CertificateValidity");
            }
            this.notBefore = derValue.data.getGeneralizedTime();
        }
        if (sequence[1].tag == 23) {
            this.notAfter = derValue.data.getUTCTime();
        }
        else {
            if (sequence[1].tag != 24) {
                throw new IOException("Invalid encoding for CertificateValidity");
            }
            this.notAfter = derValue.data.getGeneralizedTime();
        }
    }
    
    public CertificateValidity() {
    }
    
    public CertificateValidity(final Date notBefore, final Date notAfter) {
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }
    
    public CertificateValidity(final DerInputStream derInputStream) throws IOException {
        this.construct(derInputStream.getDerValue());
    }
    
    @Override
    public String toString() {
        if (this.notBefore == null || this.notAfter == null) {
            return "";
        }
        return "Validity: [From: " + this.notBefore.toString() + ",\n               To: " + this.notAfter.toString() + "]";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        if (this.notBefore == null || this.notAfter == null) {
            throw new IOException("CertAttrSet:CertificateValidity: null values to encode.\n");
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.notBefore.getTime() < 2524608000000L) {
            derOutputStream.putUTCTime(this.notBefore);
        }
        else {
            derOutputStream.putGeneralizedTime(this.notBefore);
        }
        if (this.notAfter.getTime() < 2524608000000L) {
            derOutputStream.putUTCTime(this.notAfter);
        }
        else {
            derOutputStream.putGeneralizedTime(this.notAfter);
        }
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.write((byte)48, derOutputStream);
        outputStream.write(derOutputStream2.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof Date)) {
            throw new IOException("Attribute must be of type Date.");
        }
        if (s.equalsIgnoreCase("notBefore")) {
            this.notBefore = (Date)o;
        }
        else {
            if (!s.equalsIgnoreCase("notAfter")) {
                throw new IOException("Attribute name not recognized by CertAttrSet: CertificateValidity.");
            }
            this.notAfter = (Date)o;
        }
    }
    
    @Override
    public Date get(final String s) throws IOException {
        if (s.equalsIgnoreCase("notBefore")) {
            return this.getNotBefore();
        }
        if (s.equalsIgnoreCase("notAfter")) {
            return this.getNotAfter();
        }
        throw new IOException("Attribute name not recognized by CertAttrSet: CertificateValidity.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("notBefore")) {
            this.notBefore = null;
        }
        else {
            if (!s.equalsIgnoreCase("notAfter")) {
                throw new IOException("Attribute name not recognized by CertAttrSet: CertificateValidity.");
            }
            this.notAfter = null;
        }
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("notBefore");
        attributeNameEnumeration.addElement("notAfter");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "validity";
    }
    
    public void valid() throws CertificateNotYetValidException, CertificateExpiredException {
        this.valid(new Date());
    }
    
    public void valid(final Date date) throws CertificateNotYetValidException, CertificateExpiredException {
        if (this.notBefore.after(date)) {
            throw new CertificateNotYetValidException("NotBefore: " + this.notBefore.toString());
        }
        if (this.notAfter.before(date)) {
            throw new CertificateExpiredException("NotAfter: " + this.notAfter.toString());
        }
    }
}
