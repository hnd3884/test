package sun.security.x509;

import java.util.Enumeration;
import java.io.OutputStream;
import java.util.Objects;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import java.util.Date;

public class PrivateKeyUsageExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.PrivateKeyUsage";
    public static final String NAME = "PrivateKeyUsage";
    public static final String NOT_BEFORE = "not_before";
    public static final String NOT_AFTER = "not_after";
    private static final byte TAG_BEFORE = 0;
    private static final byte TAG_AFTER = 1;
    private Date notBefore;
    private Date notAfter;
    
    private void encodeThis() throws IOException {
        if (this.notBefore == null && this.notAfter == null) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        if (this.notBefore != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putGeneralizedTime(this.notBefore);
            derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)0), derOutputStream3);
        }
        if (this.notAfter != null) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.putGeneralizedTime(this.notAfter);
            derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)1), derOutputStream4);
        }
        derOutputStream.write((byte)48, derOutputStream2);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public PrivateKeyUsageExtension(final Date notBefore, final Date notAfter) throws IOException {
        this.notBefore = null;
        this.notAfter = null;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.extensionId = PKIXExtensions.PrivateKeyUsage_Id;
        this.critical = false;
        this.encodeThis();
    }
    
    public PrivateKeyUsageExtension(final Boolean b, final Object o) throws CertificateException, IOException {
        this.notBefore = null;
        this.notAfter = null;
        this.extensionId = PKIXExtensions.PrivateKeyUsage_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        final DerValue[] sequence = new DerInputStream(this.extensionValue).getSequence(2);
        for (int i = 0; i < sequence.length; ++i) {
            final DerValue derValue = sequence[i];
            if (derValue.isContextSpecific((byte)0) && !derValue.isConstructed()) {
                if (this.notBefore != null) {
                    throw new CertificateParsingException("Duplicate notBefore in PrivateKeyUsage.");
                }
                derValue.resetTag((byte)24);
                this.notBefore = new DerInputStream(derValue.toByteArray()).getGeneralizedTime();
            }
            else {
                if (!derValue.isContextSpecific((byte)1) || derValue.isConstructed()) {
                    throw new IOException("Invalid encoding of PrivateKeyUsageExtension");
                }
                if (this.notAfter != null) {
                    throw new CertificateParsingException("Duplicate notAfter in PrivateKeyUsage.");
                }
                derValue.resetTag((byte)24);
                this.notAfter = new DerInputStream(derValue.toByteArray()).getGeneralizedTime();
            }
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + "PrivateKeyUsage: [\n" + ((this.notBefore == null) ? "" : ("From: " + this.notBefore.toString() + ", ")) + ((this.notAfter == null) ? "" : ("To: " + this.notAfter.toString())) + "]\n";
    }
    
    public void valid() throws CertificateNotYetValidException, CertificateExpiredException {
        this.valid(new Date());
    }
    
    public void valid(final Date date) throws CertificateNotYetValidException, CertificateExpiredException {
        Objects.requireNonNull(date);
        if (this.notBefore != null && this.notBefore.after(date)) {
            throw new CertificateNotYetValidException("NotBefore: " + this.notBefore.toString());
        }
        if (this.notAfter != null && this.notAfter.before(date)) {
            throw new CertificateExpiredException("NotAfter: " + this.notAfter.toString());
        }
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.PrivateKeyUsage_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws CertificateException, IOException {
        if (!(o instanceof Date)) {
            throw new CertificateException("Attribute must be of type Date.");
        }
        if (s.equalsIgnoreCase("not_before")) {
            this.notBefore = (Date)o;
        }
        else {
            if (!s.equalsIgnoreCase("not_after")) {
                throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
            }
            this.notAfter = (Date)o;
        }
        this.encodeThis();
    }
    
    @Override
    public Date get(final String s) throws CertificateException {
        if (s.equalsIgnoreCase("not_before")) {
            return new Date(this.notBefore.getTime());
        }
        if (s.equalsIgnoreCase("not_after")) {
            return new Date(this.notAfter.getTime());
        }
        throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
    }
    
    @Override
    public void delete(final String s) throws CertificateException, IOException {
        if (s.equalsIgnoreCase("not_before")) {
            this.notBefore = null;
        }
        else {
            if (!s.equalsIgnoreCase("not_after")) {
                throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
            }
            this.notAfter = null;
        }
        this.encodeThis();
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("not_before");
        attributeNameEnumeration.addElement("not_after");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "PrivateKeyUsage";
    }
}
