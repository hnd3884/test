package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;

public class PolicyConstraintsExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.PolicyConstraints";
    public static final String NAME = "PolicyConstraints";
    public static final String REQUIRE = "require";
    public static final String INHIBIT = "inhibit";
    private static final byte TAG_REQUIRE = 0;
    private static final byte TAG_INHIBIT = 1;
    private int require;
    private int inhibit;
    
    private void encodeThis() throws IOException {
        if (this.require == -1 && this.inhibit == -1) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        if (this.require != -1) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putInteger(this.require);
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)0), derOutputStream3);
        }
        if (this.inhibit != -1) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.putInteger(this.inhibit);
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)1), derOutputStream4);
        }
        derOutputStream2.write((byte)48, derOutputStream);
        this.extensionValue = derOutputStream2.toByteArray();
    }
    
    public PolicyConstraintsExtension(final int n, final int n2) throws IOException {
        this(Boolean.FALSE, n, n2);
    }
    
    public PolicyConstraintsExtension(final Boolean b, final int require, final int inhibit) throws IOException {
        this.require = -1;
        this.inhibit = -1;
        this.require = require;
        this.inhibit = inhibit;
        this.extensionId = PKIXExtensions.PolicyConstraints_Id;
        this.critical = b;
        this.encodeThis();
    }
    
    public PolicyConstraintsExtension(final Boolean b, final Object o) throws IOException {
        this.require = -1;
        this.inhibit = -1;
        this.extensionId = PKIXExtensions.PolicyConstraints_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 48) {
            throw new IOException("Sequence tag missing for PolicyConstraint.");
        }
        final DerInputStream data = derValue.data;
        while (data != null && data.available() != 0) {
            final DerValue derValue2 = data.getDerValue();
            if (derValue2.isContextSpecific((byte)0) && !derValue2.isConstructed()) {
                if (this.require != -1) {
                    throw new IOException("Duplicate requireExplicitPolicyfound in the PolicyConstraintsExtension");
                }
                derValue2.resetTag((byte)2);
                this.require = derValue2.getInteger();
            }
            else {
                if (!derValue2.isContextSpecific((byte)1) || derValue2.isConstructed()) {
                    throw new IOException("Invalid encoding of PolicyConstraint");
                }
                if (this.inhibit != -1) {
                    throw new IOException("Duplicate inhibitPolicyMappingfound in the PolicyConstraintsExtension");
                }
                derValue2.resetTag((byte)2);
                this.inhibit = derValue2.getInteger();
            }
        }
    }
    
    @Override
    public String toString() {
        final String string = super.toString() + "PolicyConstraints: [  Require: ";
        String s;
        if (this.require == -1) {
            s = string + "unspecified;";
        }
        else {
            s = string + this.require + ";";
        }
        final String string2 = s + "\tInhibit: ";
        String s2;
        if (this.inhibit == -1) {
            s2 = string2 + "unspecified";
        }
        else {
            s2 = string2 + this.inhibit;
        }
        return s2 + " ]\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.PolicyConstraints_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof Integer)) {
            throw new IOException("Attribute value should be of type Integer.");
        }
        if (s.equalsIgnoreCase("require")) {
            this.require = (int)o;
        }
        else {
            if (!s.equalsIgnoreCase("inhibit")) {
                throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:PolicyConstraints.");
            }
            this.inhibit = (int)o;
        }
        this.encodeThis();
    }
    
    @Override
    public Integer get(final String s) throws IOException {
        if (s.equalsIgnoreCase("require")) {
            return new Integer(this.require);
        }
        if (s.equalsIgnoreCase("inhibit")) {
            return new Integer(this.inhibit);
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:PolicyConstraints.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("require")) {
            this.require = -1;
        }
        else {
            if (!s.equalsIgnoreCase("inhibit")) {
                throw new IOException("Attribute name not recognized by CertAttrSet:PolicyConstraints.");
            }
            this.inhibit = -1;
        }
        this.encodeThis();
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("require");
        attributeNameEnumeration.addElement("inhibit");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "PolicyConstraints";
    }
}
