package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;
import sun.security.util.Debug;

public class InhibitAnyPolicyExtension extends Extension implements CertAttrSet<String>
{
    private static final Debug debug;
    public static final String IDENT = "x509.info.extensions.InhibitAnyPolicy";
    public static ObjectIdentifier AnyPolicy_Id;
    public static final String NAME = "InhibitAnyPolicy";
    public static final String SKIP_CERTS = "skip_certs";
    private int skipCerts;
    
    private void encodeThis() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(this.skipCerts);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public InhibitAnyPolicyExtension(final int skipCerts) throws IOException {
        this.skipCerts = Integer.MAX_VALUE;
        if (skipCerts < -1) {
            throw new IOException("Invalid value for skipCerts");
        }
        if (skipCerts == -1) {
            this.skipCerts = Integer.MAX_VALUE;
        }
        else {
            this.skipCerts = skipCerts;
        }
        this.extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
        this.critical = true;
        this.encodeThis();
    }
    
    public InhibitAnyPolicyExtension(final Boolean b, final Object o) throws IOException {
        this.skipCerts = Integer.MAX_VALUE;
        this.extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
        if (!b) {
            throw new IOException("Criticality cannot be false for InhibitAnyPolicy");
        }
        this.critical = b;
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 2) {
            throw new IOException("Invalid encoding of InhibitAnyPolicy: data not integer");
        }
        if (derValue.data == null) {
            throw new IOException("Invalid encoding of InhibitAnyPolicy: null data");
        }
        final int integer = derValue.getInteger();
        if (integer < -1) {
            throw new IOException("Invalid value for skipCerts");
        }
        if (integer == -1) {
            this.skipCerts = Integer.MAX_VALUE;
        }
        else {
            this.skipCerts = integer;
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + "InhibitAnyPolicy: " + this.skipCerts + "\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
            this.critical = true;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!s.equalsIgnoreCase("skip_certs")) {
            throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
        }
        if (!(o instanceof Integer)) {
            throw new IOException("Attribute value should be of type Integer.");
        }
        final int intValue = (int)o;
        if (intValue < -1) {
            throw new IOException("Invalid value for skipCerts");
        }
        if (intValue == -1) {
            this.skipCerts = Integer.MAX_VALUE;
        }
        else {
            this.skipCerts = intValue;
        }
        this.encodeThis();
    }
    
    @Override
    public Integer get(final String s) throws IOException {
        if (s.equalsIgnoreCase("skip_certs")) {
            return new Integer(this.skipCerts);
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("skip_certs")) {
            throw new IOException("Attribute skip_certs may not be deleted.");
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("skip_certs");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "InhibitAnyPolicy";
    }
    
    static {
        debug = Debug.getInstance("certpath");
        try {
            InhibitAnyPolicyExtension.AnyPolicy_Id = new ObjectIdentifier("2.5.29.32.0");
        }
        catch (final IOException ex) {}
    }
}
