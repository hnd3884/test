package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import java.util.ArrayList;
import sun.security.util.DerValue;
import java.util.Collections;
import java.io.IOException;
import java.util.Iterator;
import sun.security.util.DerOutputStream;
import java.util.List;

public class PolicyMappingsExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.PolicyMappings";
    public static final String NAME = "PolicyMappings";
    public static final String MAP = "map";
    private List<CertificatePolicyMap> maps;
    
    private void encodeThis() throws IOException {
        if (this.maps == null || this.maps.isEmpty()) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        final Iterator<CertificatePolicyMap> iterator = this.maps.iterator();
        while (iterator.hasNext()) {
            iterator.next().encode(derOutputStream2);
        }
        derOutputStream.write((byte)48, derOutputStream2);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public PolicyMappingsExtension(final List<CertificatePolicyMap> maps) throws IOException {
        this.maps = maps;
        this.extensionId = PKIXExtensions.PolicyMappings_Id;
        this.critical = false;
        this.encodeThis();
    }
    
    public PolicyMappingsExtension() {
        this.extensionId = PKIXExtensions.KeyUsage_Id;
        this.critical = false;
        this.maps = Collections.emptyList();
    }
    
    public PolicyMappingsExtension(final Boolean b, final Object o) throws IOException {
        this.extensionId = PKIXExtensions.PolicyMappings_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for PolicyMappingsExtension.");
        }
        this.maps = new ArrayList<CertificatePolicyMap>();
        while (derValue.data.available() != 0) {
            this.maps.add(new CertificatePolicyMap(derValue.data.getDerValue()));
        }
    }
    
    @Override
    public String toString() {
        if (this.maps == null) {
            return "";
        }
        return super.toString() + "PolicyMappings [\n" + this.maps.toString() + "]\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.PolicyMappings_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!s.equalsIgnoreCase("map")) {
            throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
        }
        if (!(o instanceof List)) {
            throw new IOException("Attribute value should be of type List.");
        }
        this.maps = (List)o;
        this.encodeThis();
    }
    
    @Override
    public List<CertificatePolicyMap> get(final String s) throws IOException {
        if (s.equalsIgnoreCase("map")) {
            return this.maps;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("map")) {
            this.maps = null;
            this.encodeThis();
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("map");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "PolicyMappings";
    }
}
