package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collections;
import sun.security.util.DerValue;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.security.cert.PolicyQualifierInfo;
import java.util.Set;

public class PolicyInformation
{
    public static final String NAME = "PolicyInformation";
    public static final String ID = "id";
    public static final String QUALIFIERS = "qualifiers";
    private CertificatePolicyId policyIdentifier;
    private Set<PolicyQualifierInfo> policyQualifiers;
    
    public PolicyInformation(final CertificatePolicyId policyIdentifier, final Set<PolicyQualifierInfo> set) throws IOException {
        if (set == null) {
            throw new NullPointerException("policyQualifiers is null");
        }
        this.policyQualifiers = new LinkedHashSet<PolicyQualifierInfo>(set);
        this.policyIdentifier = policyIdentifier;
    }
    
    public PolicyInformation(final DerValue derValue) throws IOException {
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding of PolicyInformation");
        }
        this.policyIdentifier = new CertificatePolicyId(derValue.data.getDerValue());
        if (derValue.data.available() != 0) {
            this.policyQualifiers = new LinkedHashSet<PolicyQualifierInfo>();
            final DerValue derValue2 = derValue.data.getDerValue();
            if (derValue2.tag != 48) {
                throw new IOException("Invalid encoding of PolicyInformation");
            }
            if (derValue2.data.available() == 0) {
                throw new IOException("No data available in policyQualifiers");
            }
            while (derValue2.data.available() != 0) {
                this.policyQualifiers.add(new PolicyQualifierInfo(derValue2.data.getDerValue().toByteArray()));
            }
        }
        else {
            this.policyQualifiers = Collections.emptySet();
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PolicyInformation)) {
            return false;
        }
        final PolicyInformation policyInformation = (PolicyInformation)o;
        return this.policyIdentifier.equals(policyInformation.getPolicyIdentifier()) && this.policyQualifiers.equals(policyInformation.getPolicyQualifiers());
    }
    
    @Override
    public int hashCode() {
        return 37 * (37 + this.policyIdentifier.hashCode()) + this.policyQualifiers.hashCode();
    }
    
    public CertificatePolicyId getPolicyIdentifier() {
        return this.policyIdentifier;
    }
    
    public Set<PolicyQualifierInfo> getPolicyQualifiers() {
        return this.policyQualifiers;
    }
    
    public Object get(final String s) throws IOException {
        if (s.equalsIgnoreCase("id")) {
            return this.policyIdentifier;
        }
        if (s.equalsIgnoreCase("qualifiers")) {
            return this.policyQualifiers;
        }
        throw new IOException("Attribute name [" + s + "] not recognized by PolicyInformation.");
    }
    
    public void set(final String s, final Object o) throws IOException {
        if (s.equalsIgnoreCase("id")) {
            if (!(o instanceof CertificatePolicyId)) {
                throw new IOException("Attribute value must be instance of CertificatePolicyId.");
            }
            this.policyIdentifier = (CertificatePolicyId)o;
        }
        else {
            if (!s.equalsIgnoreCase("qualifiers")) {
                throw new IOException("Attribute name [" + s + "] not recognized by PolicyInformation");
            }
            if (this.policyIdentifier == null) {
                throw new IOException("Attribute must have a CertificatePolicyIdentifier value before PolicyQualifierInfo can be set.");
            }
            if (!(o instanceof Set)) {
                throw new IOException("Attribute value must be of type Set.");
            }
            final Iterator iterator = ((Set)o).iterator();
            while (iterator.hasNext()) {
                if (!(iterator.next() instanceof PolicyQualifierInfo)) {
                    throw new IOException("Attribute value must be aSet of PolicyQualifierInfo objects.");
                }
            }
            this.policyQualifiers = (Set)o;
        }
    }
    
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("qualifiers")) {
            this.policyQualifiers = Collections.emptySet();
            return;
        }
        if (s.equalsIgnoreCase("id")) {
            throw new IOException("Attribute ID may not be deleted from PolicyInformation.");
        }
        throw new IOException("Attribute name [" + s + "] not recognized by PolicyInformation.");
    }
    
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("id");
        attributeNameEnumeration.addElement("qualifiers");
        return attributeNameEnumeration.elements();
    }
    
    public String getName() {
        return "PolicyInformation";
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("  [" + this.policyIdentifier.toString());
        sb.append(this.policyQualifiers + "  ]\n");
        return sb.toString();
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        this.policyIdentifier.encode(derOutputStream2);
        if (!this.policyQualifiers.isEmpty()) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            final Iterator<PolicyQualifierInfo> iterator = this.policyQualifiers.iterator();
            while (iterator.hasNext()) {
                derOutputStream3.write(iterator.next().getEncoded());
            }
            derOutputStream2.write((byte)48, derOutputStream3);
        }
        derOutputStream.write((byte)48, derOutputStream2);
    }
}
