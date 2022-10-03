package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Collections;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import java.io.IOException;
import java.util.List;

public class CRLDistributionPointsExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.CRLDistributionPoints";
    public static final String NAME = "CRLDistributionPoints";
    public static final String POINTS = "points";
    private List<DistributionPoint> distributionPoints;
    private String extensionName;
    
    public CRLDistributionPointsExtension(final List<DistributionPoint> list) throws IOException {
        this(false, list);
    }
    
    public CRLDistributionPointsExtension(final boolean b, final List<DistributionPoint> list) throws IOException {
        this(PKIXExtensions.CRLDistributionPoints_Id, b, list, "CRLDistributionPoints");
    }
    
    protected CRLDistributionPointsExtension(final ObjectIdentifier extensionId, final boolean critical, final List<DistributionPoint> distributionPoints, final String extensionName) throws IOException {
        this.extensionId = extensionId;
        this.critical = critical;
        this.distributionPoints = distributionPoints;
        this.encodeThis();
        this.extensionName = extensionName;
    }
    
    public CRLDistributionPointsExtension(final Boolean b, final Object o) throws IOException {
        this(PKIXExtensions.CRLDistributionPoints_Id, b, o, "CRLDistributionPoints");
    }
    
    protected CRLDistributionPointsExtension(final ObjectIdentifier extensionId, final Boolean b, final Object o, final String extensionName) throws IOException {
        this.extensionId = extensionId;
        this.critical = b;
        if (!(o instanceof byte[])) {
            throw new IOException("Illegal argument type");
        }
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for " + extensionName + " extension.");
        }
        this.distributionPoints = new ArrayList<DistributionPoint>();
        while (derValue.data.available() != 0) {
            this.distributionPoints.add(new DistributionPoint(derValue.data.getDerValue()));
        }
        this.extensionName = extensionName;
    }
    
    @Override
    public String getName() {
        return this.extensionName;
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        this.encode(outputStream, PKIXExtensions.CRLDistributionPoints_Id, false);
    }
    
    protected void encode(final OutputStream outputStream, final ObjectIdentifier extensionId, final boolean critical) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = extensionId;
            this.critical = critical;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!s.equalsIgnoreCase("points")) {
            throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:" + this.extensionName + ".");
        }
        if (!(o instanceof List)) {
            throw new IOException("Attribute value should be of type List.");
        }
        this.distributionPoints = (List)o;
        this.encodeThis();
    }
    
    @Override
    public List<DistributionPoint> get(final String s) throws IOException {
        if (s.equalsIgnoreCase("points")) {
            return this.distributionPoints;
        }
        throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:" + this.extensionName + ".");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("points")) {
            this.distributionPoints = Collections.emptyList();
            this.encodeThis();
            return;
        }
        throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:" + this.extensionName + '.');
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("points");
        return attributeNameEnumeration.elements();
    }
    
    private void encodeThis() throws IOException {
        if (this.distributionPoints.isEmpty()) {
            this.extensionValue = null;
        }
        else {
            final DerOutputStream derOutputStream = new DerOutputStream();
            final Iterator<DistributionPoint> iterator = this.distributionPoints.iterator();
            while (iterator.hasNext()) {
                iterator.next().encode(derOutputStream);
            }
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            derOutputStream2.write((byte)48, derOutputStream);
            this.extensionValue = derOutputStream2.toByteArray();
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + this.extensionName + " [\n  " + this.distributionPoints + "]\n";
    }
}
