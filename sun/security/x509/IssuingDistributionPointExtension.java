package sun.security.x509;

import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import java.io.IOException;

public class IssuingDistributionPointExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.IssuingDistributionPoint";
    public static final String NAME = "IssuingDistributionPoint";
    public static final String POINT = "point";
    public static final String REASONS = "reasons";
    public static final String ONLY_USER_CERTS = "only_user_certs";
    public static final String ONLY_CA_CERTS = "only_ca_certs";
    public static final String ONLY_ATTRIBUTE_CERTS = "only_attribute_certs";
    public static final String INDIRECT_CRL = "indirect_crl";
    private DistributionPointName distributionPoint;
    private ReasonFlags revocationReasons;
    private boolean hasOnlyUserCerts;
    private boolean hasOnlyCACerts;
    private boolean hasOnlyAttributeCerts;
    private boolean isIndirectCRL;
    private static final byte TAG_DISTRIBUTION_POINT = 0;
    private static final byte TAG_ONLY_USER_CERTS = 1;
    private static final byte TAG_ONLY_CA_CERTS = 2;
    private static final byte TAG_ONLY_SOME_REASONS = 3;
    private static final byte TAG_INDIRECT_CRL = 4;
    private static final byte TAG_ONLY_ATTRIBUTE_CERTS = 5;
    
    public IssuingDistributionPointExtension(final DistributionPointName distributionPoint, final ReasonFlags revocationReasons, final boolean hasOnlyUserCerts, final boolean hasOnlyCACerts, final boolean hasOnlyAttributeCerts, final boolean isIndirectCRL) throws IOException {
        this.distributionPoint = null;
        this.revocationReasons = null;
        this.hasOnlyUserCerts = false;
        this.hasOnlyCACerts = false;
        this.hasOnlyAttributeCerts = false;
        this.isIndirectCRL = false;
        if ((hasOnlyUserCerts && (hasOnlyCACerts || hasOnlyAttributeCerts)) || (hasOnlyCACerts && (hasOnlyUserCerts || hasOnlyAttributeCerts)) || (hasOnlyAttributeCerts && (hasOnlyUserCerts || hasOnlyCACerts))) {
            throw new IllegalArgumentException("Only one of hasOnlyUserCerts, hasOnlyCACerts, hasOnlyAttributeCerts may be set to true");
        }
        this.extensionId = PKIXExtensions.IssuingDistributionPoint_Id;
        this.critical = true;
        this.distributionPoint = distributionPoint;
        this.revocationReasons = revocationReasons;
        this.hasOnlyUserCerts = hasOnlyUserCerts;
        this.hasOnlyCACerts = hasOnlyCACerts;
        this.hasOnlyAttributeCerts = hasOnlyAttributeCerts;
        this.isIndirectCRL = isIndirectCRL;
        this.encodeThis();
    }
    
    public IssuingDistributionPointExtension(final Boolean b, final Object o) throws IOException {
        this.distributionPoint = null;
        this.revocationReasons = null;
        this.hasOnlyUserCerts = false;
        this.hasOnlyCACerts = false;
        this.hasOnlyAttributeCerts = false;
        this.isIndirectCRL = false;
        this.extensionId = PKIXExtensions.IssuingDistributionPoint_Id;
        this.critical = b;
        if (!(o instanceof byte[])) {
            throw new IOException("Illegal argument type");
        }
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for IssuingDistributionPointExtension.");
        }
        if (derValue.data == null || derValue.data.available() == 0) {
            return;
        }
        final DerInputStream data = derValue.data;
        while (data != null && data.available() != 0) {
            final DerValue derValue2 = data.getDerValue();
            if (derValue2.isContextSpecific((byte)0) && derValue2.isConstructed()) {
                this.distributionPoint = new DistributionPointName(derValue2.data.getDerValue());
            }
            else if (derValue2.isContextSpecific((byte)1) && !derValue2.isConstructed()) {
                derValue2.resetTag((byte)1);
                this.hasOnlyUserCerts = derValue2.getBoolean();
            }
            else if (derValue2.isContextSpecific((byte)2) && !derValue2.isConstructed()) {
                derValue2.resetTag((byte)1);
                this.hasOnlyCACerts = derValue2.getBoolean();
            }
            else if (derValue2.isContextSpecific((byte)3) && !derValue2.isConstructed()) {
                this.revocationReasons = new ReasonFlags(derValue2);
            }
            else if (derValue2.isContextSpecific((byte)4) && !derValue2.isConstructed()) {
                derValue2.resetTag((byte)1);
                this.isIndirectCRL = derValue2.getBoolean();
            }
            else {
                if (!derValue2.isContextSpecific((byte)5) || derValue2.isConstructed()) {
                    throw new IOException("Invalid encoding of IssuingDistributionPoint");
                }
                derValue2.resetTag((byte)1);
                this.hasOnlyAttributeCerts = derValue2.getBoolean();
            }
        }
    }
    
    @Override
    public String getName() {
        return "IssuingDistributionPoint";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.IssuingDistributionPoint_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (s.equalsIgnoreCase("point")) {
            if (!(o instanceof DistributionPointName)) {
                throw new IOException("Attribute value should be of type DistributionPointName.");
            }
            this.distributionPoint = (DistributionPointName)o;
        }
        else if (s.equalsIgnoreCase("reasons")) {
            if (!(o instanceof ReasonFlags)) {
                throw new IOException("Attribute value should be of type ReasonFlags.");
            }
            this.revocationReasons = (ReasonFlags)o;
        }
        else if (s.equalsIgnoreCase("indirect_crl")) {
            if (!(o instanceof Boolean)) {
                throw new IOException("Attribute value should be of type Boolean.");
            }
            this.isIndirectCRL = (boolean)o;
        }
        else if (s.equalsIgnoreCase("only_user_certs")) {
            if (!(o instanceof Boolean)) {
                throw new IOException("Attribute value should be of type Boolean.");
            }
            this.hasOnlyUserCerts = (boolean)o;
        }
        else if (s.equalsIgnoreCase("only_ca_certs")) {
            if (!(o instanceof Boolean)) {
                throw new IOException("Attribute value should be of type Boolean.");
            }
            this.hasOnlyCACerts = (boolean)o;
        }
        else {
            if (!s.equalsIgnoreCase("only_attribute_certs")) {
                throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:IssuingDistributionPointExtension.");
            }
            if (!(o instanceof Boolean)) {
                throw new IOException("Attribute value should be of type Boolean.");
            }
            this.hasOnlyAttributeCerts = (boolean)o;
        }
        this.encodeThis();
    }
    
    @Override
    public Object get(final String s) throws IOException {
        if (s.equalsIgnoreCase("point")) {
            return this.distributionPoint;
        }
        if (s.equalsIgnoreCase("indirect_crl")) {
            return this.isIndirectCRL;
        }
        if (s.equalsIgnoreCase("reasons")) {
            return this.revocationReasons;
        }
        if (s.equalsIgnoreCase("only_user_certs")) {
            return this.hasOnlyUserCerts;
        }
        if (s.equalsIgnoreCase("only_ca_certs")) {
            return this.hasOnlyCACerts;
        }
        if (s.equalsIgnoreCase("only_attribute_certs")) {
            return this.hasOnlyAttributeCerts;
        }
        throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:IssuingDistributionPointExtension.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("point")) {
            this.distributionPoint = null;
        }
        else if (s.equalsIgnoreCase("indirect_crl")) {
            this.isIndirectCRL = false;
        }
        else if (s.equalsIgnoreCase("reasons")) {
            this.revocationReasons = null;
        }
        else if (s.equalsIgnoreCase("only_user_certs")) {
            this.hasOnlyUserCerts = false;
        }
        else if (s.equalsIgnoreCase("only_ca_certs")) {
            this.hasOnlyCACerts = false;
        }
        else {
            if (!s.equalsIgnoreCase("only_attribute_certs")) {
                throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:IssuingDistributionPointExtension.");
            }
            this.hasOnlyAttributeCerts = false;
        }
        this.encodeThis();
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("point");
        attributeNameEnumeration.addElement("reasons");
        attributeNameEnumeration.addElement("only_user_certs");
        attributeNameEnumeration.addElement("only_ca_certs");
        attributeNameEnumeration.addElement("only_attribute_certs");
        attributeNameEnumeration.addElement("indirect_crl");
        return attributeNameEnumeration.elements();
    }
    
    private void encodeThis() throws IOException {
        if (this.distributionPoint == null && this.revocationReasons == null && !this.hasOnlyUserCerts && !this.hasOnlyCACerts && !this.hasOnlyAttributeCerts && !this.isIndirectCRL) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.distributionPoint != null) {
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            this.distributionPoint.encode(derOutputStream2);
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        }
        if (this.hasOnlyUserCerts) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putBoolean(this.hasOnlyUserCerts);
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)1), derOutputStream3);
        }
        if (this.hasOnlyCACerts) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.putBoolean(this.hasOnlyCACerts);
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)2), derOutputStream4);
        }
        if (this.revocationReasons != null) {
            final DerOutputStream derOutputStream5 = new DerOutputStream();
            this.revocationReasons.encode(derOutputStream5);
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)3), derOutputStream5);
        }
        if (this.isIndirectCRL) {
            final DerOutputStream derOutputStream6 = new DerOutputStream();
            derOutputStream6.putBoolean(this.isIndirectCRL);
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)4), derOutputStream6);
        }
        if (this.hasOnlyAttributeCerts) {
            final DerOutputStream derOutputStream7 = new DerOutputStream();
            derOutputStream7.putBoolean(this.hasOnlyAttributeCerts);
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)5), derOutputStream7);
        }
        final DerOutputStream derOutputStream8 = new DerOutputStream();
        derOutputStream8.write((byte)48, derOutputStream);
        this.extensionValue = derOutputStream8.toByteArray();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("IssuingDistributionPoint [\n  ");
        if (this.distributionPoint != null) {
            sb.append(this.distributionPoint);
        }
        if (this.revocationReasons != null) {
            sb.append(this.revocationReasons);
        }
        sb.append(this.hasOnlyUserCerts ? "  Only contains user certs: true" : "  Only contains user certs: false").append("\n");
        sb.append(this.hasOnlyCACerts ? "  Only contains CA certs: true" : "  Only contains CA certs: false").append("\n");
        sb.append(this.hasOnlyAttributeCerts ? "  Only contains attribute certs: true" : "  Only contains attribute certs: false").append("\n");
        sb.append(this.isIndirectCRL ? "  Indirect CRL: true" : "  Indirect CRL: false").append("\n");
        sb.append("]\n");
        return sb.toString();
    }
}
