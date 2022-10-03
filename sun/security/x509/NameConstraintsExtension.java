package sun.security.x509;

import java.util.Iterator;
import sun.net.util.IPAddressUtil;
import sun.security.pkcs.PKCS9Attribute;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import java.io.IOException;

public class NameConstraintsExtension extends Extension implements CertAttrSet<String>, Cloneable
{
    public static final String IDENT = "x509.info.extensions.NameConstraints";
    public static final String NAME = "NameConstraints";
    public static final String PERMITTED_SUBTREES = "permitted_subtrees";
    public static final String EXCLUDED_SUBTREES = "excluded_subtrees";
    private static final byte TAG_PERMITTED = 0;
    private static final byte TAG_EXCLUDED = 1;
    private GeneralSubtrees permitted;
    private GeneralSubtrees excluded;
    private boolean hasMin;
    private boolean hasMax;
    private boolean minMaxValid;
    
    private void calcMinMax() throws IOException {
        this.hasMin = false;
        this.hasMax = false;
        if (this.excluded != null) {
            for (int i = 0; i < this.excluded.size(); ++i) {
                final GeneralSubtree value = this.excluded.get(i);
                if (value.getMinimum() != 0) {
                    this.hasMin = true;
                }
                if (value.getMaximum() != -1) {
                    this.hasMax = true;
                }
            }
        }
        if (this.permitted != null) {
            for (int j = 0; j < this.permitted.size(); ++j) {
                final GeneralSubtree value2 = this.permitted.get(j);
                if (value2.getMinimum() != 0) {
                    this.hasMin = true;
                }
                if (value2.getMaximum() != -1) {
                    this.hasMax = true;
                }
            }
        }
        this.minMaxValid = true;
    }
    
    private void encodeThis() throws IOException {
        this.minMaxValid = false;
        if (this.permitted == null && this.excluded == null) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        if (this.permitted != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            this.permitted.encode(derOutputStream3);
            derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream3);
        }
        if (this.excluded != null) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            this.excluded.encode(derOutputStream4);
            derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream4);
        }
        derOutputStream.write((byte)48, derOutputStream2);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public NameConstraintsExtension(final GeneralSubtrees permitted, final GeneralSubtrees excluded) throws IOException {
        this.permitted = null;
        this.excluded = null;
        this.minMaxValid = false;
        this.permitted = permitted;
        this.excluded = excluded;
        this.extensionId = PKIXExtensions.NameConstraints_Id;
        this.critical = true;
        this.encodeThis();
    }
    
    public NameConstraintsExtension(final Boolean b, final Object o) throws IOException {
        this.permitted = null;
        this.excluded = null;
        this.minMaxValid = false;
        this.extensionId = PKIXExtensions.NameConstraints_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for NameConstraintsExtension.");
        }
        if (derValue.data == null) {
            return;
        }
        while (derValue.data.available() != 0) {
            final DerValue derValue2 = derValue.data.getDerValue();
            if (derValue2.isContextSpecific((byte)0) && derValue2.isConstructed()) {
                if (this.permitted != null) {
                    throw new IOException("Duplicate permitted GeneralSubtrees in NameConstraintsExtension.");
                }
                derValue2.resetTag((byte)48);
                this.permitted = new GeneralSubtrees(derValue2);
            }
            else {
                if (!derValue2.isContextSpecific((byte)1) || !derValue2.isConstructed()) {
                    throw new IOException("Invalid encoding of NameConstraintsExtension.");
                }
                if (this.excluded != null) {
                    throw new IOException("Duplicate excluded GeneralSubtrees in NameConstraintsExtension.");
                }
                derValue2.resetTag((byte)48);
                this.excluded = new GeneralSubtrees(derValue2);
            }
        }
        this.minMaxValid = false;
    }
    
    @Override
    public String toString() {
        return super.toString() + "NameConstraints: [" + ((this.permitted == null) ? "" : ("\n    Permitted:" + this.permitted.toString())) + ((this.excluded == null) ? "" : ("\n    Excluded:" + this.excluded.toString())) + "   ]\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.NameConstraints_Id;
            this.critical = true;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (s.equalsIgnoreCase("permitted_subtrees")) {
            if (!(o instanceof GeneralSubtrees)) {
                throw new IOException("Attribute value should be of type GeneralSubtrees.");
            }
            this.permitted = (GeneralSubtrees)o;
        }
        else {
            if (!s.equalsIgnoreCase("excluded_subtrees")) {
                throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
            }
            if (!(o instanceof GeneralSubtrees)) {
                throw new IOException("Attribute value should be of type GeneralSubtrees.");
            }
            this.excluded = (GeneralSubtrees)o;
        }
        this.encodeThis();
    }
    
    @Override
    public GeneralSubtrees get(final String s) throws IOException {
        if (s.equalsIgnoreCase("permitted_subtrees")) {
            return this.permitted;
        }
        if (s.equalsIgnoreCase("excluded_subtrees")) {
            return this.excluded;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("permitted_subtrees")) {
            this.permitted = null;
        }
        else {
            if (!s.equalsIgnoreCase("excluded_subtrees")) {
                throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
            }
            this.excluded = null;
        }
        this.encodeThis();
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("permitted_subtrees");
        attributeNameEnumeration.addElement("excluded_subtrees");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "NameConstraints";
    }
    
    public void merge(final NameConstraintsExtension nameConstraintsExtension) throws IOException {
        if (nameConstraintsExtension == null) {
            return;
        }
        final GeneralSubtrees value = nameConstraintsExtension.get("excluded_subtrees");
        if (this.excluded == null) {
            this.excluded = ((value != null) ? ((GeneralSubtrees)value.clone()) : null);
        }
        else if (value != null) {
            this.excluded.union(value);
        }
        final GeneralSubtrees value2 = nameConstraintsExtension.get("permitted_subtrees");
        if (this.permitted == null) {
            this.permitted = ((value2 != null) ? ((GeneralSubtrees)value2.clone()) : null);
        }
        else if (value2 != null) {
            final GeneralSubtrees intersect = this.permitted.intersect(value2);
            if (intersect != null) {
                if (this.excluded != null) {
                    this.excluded.union(intersect);
                }
                else {
                    this.excluded = (GeneralSubtrees)intersect.clone();
                }
            }
        }
        if (this.permitted != null) {
            this.permitted.reduce(this.excluded);
        }
        this.encodeThis();
    }
    
    public boolean verify(final X509Certificate x509Certificate) throws IOException {
        if (x509Certificate == null) {
            throw new IOException("Certificate is null");
        }
        if (!this.minMaxValid) {
            this.calcMinMax();
        }
        if (this.hasMin) {
            throw new IOException("Non-zero minimum BaseDistance in name constraints not supported");
        }
        if (this.hasMax) {
            throw new IOException("Maximum BaseDistance in name constraints not supported");
        }
        final X500Name x500Name = X500Name.asX500Name(x509Certificate.getSubjectX500Principal());
        if (!x500Name.isEmpty() && !this.verify(x500Name)) {
            return false;
        }
        GeneralNames value = null;
        try {
            final SubjectAlternativeNameExtension subjectAlternativeNameExtension = X509CertImpl.toImpl(x509Certificate).getSubjectAlternativeNameExtension();
            if (subjectAlternativeNameExtension != null) {
                value = subjectAlternativeNameExtension.get("subject_name");
            }
        }
        catch (final CertificateException ex) {
            throw new IOException("Unable to extract extensions from certificate: " + ex.getMessage());
        }
        if (value == null) {
            value = new GeneralNames();
            for (final AVA ava : x500Name.allAvas()) {
                if (ava.getObjectIdentifier().equals(PKCS9Attribute.EMAIL_ADDRESS_OID)) {
                    final String valueString = ava.getValueString();
                    if (valueString == null) {
                        continue;
                    }
                    try {
                        value.add(new GeneralName(new RFC822Name(valueString)));
                    }
                    catch (final IOException ex2) {}
                }
            }
        }
        final DerValue mostSpecificAttribute = x500Name.findMostSpecificAttribute(X500Name.commonName_oid);
        final String s = (mostSpecificAttribute == null) ? null : mostSpecificAttribute.getAsString();
        if (s != null) {
            try {
                if (IPAddressUtil.isIPv4LiteralAddress(s) || IPAddressUtil.isIPv6LiteralAddress(s)) {
                    if (!hasNameType(value, 7)) {
                        value.add(new GeneralName(new IPAddressName(s)));
                    }
                }
                else if (!hasNameType(value, 2)) {
                    value.add(new GeneralName(new DNSName(s)));
                }
            }
            catch (final IOException ex3) {}
        }
        for (int i = 0; i < value.size(); ++i) {
            if (!this.verify(value.get(i).getName())) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean hasNameType(final GeneralNames generalNames, final int n) {
        final Iterator<GeneralName> iterator = generalNames.names().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getType() == n) {
                return true;
            }
        }
        return false;
    }
    
    public boolean verify(final GeneralNameInterface generalNameInterface) throws IOException {
        if (generalNameInterface == null) {
            throw new IOException("name is null");
        }
        if (this.excluded != null && this.excluded.size() > 0) {
            for (int i = 0; i < this.excluded.size(); ++i) {
                final GeneralSubtree value = this.excluded.get(i);
                if (value != null) {
                    final GeneralName name = value.getName();
                    if (name != null) {
                        final GeneralNameInterface name2 = name.getName();
                        if (name2 != null) {
                            switch (name2.constrains(generalNameInterface)) {
                                case 0:
                                case 1: {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.permitted != null && this.permitted.size() > 0) {
            boolean b = false;
            for (int j = 0; j < this.permitted.size(); ++j) {
                final GeneralSubtree value2 = this.permitted.get(j);
                if (value2 != null) {
                    final GeneralName name3 = value2.getName();
                    if (name3 != null) {
                        final GeneralNameInterface name4 = name3.getName();
                        if (name4 != null) {
                            switch (name4.constrains(generalNameInterface)) {
                                case 2:
                                case 3: {
                                    b = true;
                                    break;
                                }
                                case 0:
                                case 1: {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            if (b) {
                return false;
            }
        }
        return true;
    }
    
    public Object clone() {
        try {
            final NameConstraintsExtension nameConstraintsExtension = (NameConstraintsExtension)super.clone();
            if (this.permitted != null) {
                nameConstraintsExtension.permitted = (GeneralSubtrees)this.permitted.clone();
            }
            if (this.excluded != null) {
                nameConstraintsExtension.excluded = (GeneralSubtrees)this.excluded.clone();
            }
            return nameConstraintsExtension;
        }
        catch (final CloneNotSupportedException ex) {
            throw new RuntimeException("CloneNotSupportedException while cloning NameConstraintsException. This should never happen.");
        }
    }
}
