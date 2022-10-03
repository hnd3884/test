package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import java.util.ArrayList;
import sun.security.util.DerValue;
import java.io.IOException;
import java.util.Iterator;
import sun.security.util.DerOutputStream;
import java.util.List;

public class CertificatePoliciesExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.CertificatePolicies";
    public static final String NAME = "CertificatePolicies";
    public static final String POLICIES = "policies";
    private List<PolicyInformation> certPolicies;
    
    private void encodeThis() throws IOException {
        if (this.certPolicies == null || this.certPolicies.isEmpty()) {
            this.extensionValue = null;
        }
        else {
            final DerOutputStream derOutputStream = new DerOutputStream();
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            final Iterator<PolicyInformation> iterator = this.certPolicies.iterator();
            while (iterator.hasNext()) {
                iterator.next().encode(derOutputStream2);
            }
            derOutputStream.write((byte)48, derOutputStream2);
            this.extensionValue = derOutputStream.toByteArray();
        }
    }
    
    public CertificatePoliciesExtension(final List<PolicyInformation> list) throws IOException {
        this(Boolean.FALSE, list);
    }
    
    public CertificatePoliciesExtension(final Boolean b, final List<PolicyInformation> certPolicies) throws IOException {
        this.certPolicies = certPolicies;
        this.extensionId = PKIXExtensions.CertificatePolicies_Id;
        this.critical = b;
        this.encodeThis();
    }
    
    public CertificatePoliciesExtension(final Boolean b, final Object o) throws IOException {
        this.extensionId = PKIXExtensions.CertificatePolicies_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for CertificatePoliciesExtension.");
        }
        this.certPolicies = new ArrayList<PolicyInformation>();
        while (derValue.data.available() != 0) {
            this.certPolicies.add(new PolicyInformation(derValue.data.getDerValue()));
        }
    }
    
    @Override
    public String toString() {
        if (this.certPolicies == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("CertificatePolicies [\n");
        final Iterator<PolicyInformation> iterator = this.certPolicies.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
        }
        sb.append("]\n");
        return sb.toString();
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.CertificatePolicies_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!s.equalsIgnoreCase("policies")) {
            throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
        }
        if (!(o instanceof List)) {
            throw new IOException("Attribute value should be of type List.");
        }
        this.certPolicies = (List)o;
        this.encodeThis();
    }
    
    @Override
    public List<PolicyInformation> get(final String s) throws IOException {
        if (s.equalsIgnoreCase("policies")) {
            return this.certPolicies;
        }
        throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("policies")) {
            this.certPolicies = null;
            this.encodeThis();
            return;
        }
        throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("policies");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "CertificatePolicies";
    }
}
