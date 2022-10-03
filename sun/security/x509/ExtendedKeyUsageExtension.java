package sun.security.x509;

import java.util.HashMap;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.io.OutputStream;
import java.util.Iterator;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerOutputStream;
import java.util.Vector;
import sun.security.util.ObjectIdentifier;
import java.util.Map;

public class ExtendedKeyUsageExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.ExtendedKeyUsage";
    public static final String NAME = "ExtendedKeyUsage";
    public static final String USAGES = "usages";
    private static final Map<ObjectIdentifier, String> map;
    private static final int[] anyExtendedKeyUsageOidData;
    private static final int[] serverAuthOidData;
    private static final int[] clientAuthOidData;
    private static final int[] codeSigningOidData;
    private static final int[] emailProtectionOidData;
    private static final int[] ipsecEndSystemOidData;
    private static final int[] ipsecTunnelOidData;
    private static final int[] ipsecUserOidData;
    private static final int[] timeStampingOidData;
    private static final int[] OCSPSigningOidData;
    private Vector<ObjectIdentifier> keyUsages;
    
    private void encodeThis() throws IOException {
        if (this.keyUsages == null || this.keyUsages.isEmpty()) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        for (int i = 0; i < this.keyUsages.size(); ++i) {
            derOutputStream2.putOID(this.keyUsages.elementAt(i));
        }
        derOutputStream.write((byte)48, derOutputStream2);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public ExtendedKeyUsageExtension(final Vector<ObjectIdentifier> vector) throws IOException {
        this(Boolean.FALSE, vector);
    }
    
    public ExtendedKeyUsageExtension(final Boolean b, final Vector<ObjectIdentifier> keyUsages) throws IOException {
        this.keyUsages = keyUsages;
        this.extensionId = PKIXExtensions.ExtendedKeyUsage_Id;
        this.critical = b;
        this.encodeThis();
    }
    
    public ExtendedKeyUsageExtension(final Boolean b, final Object o) throws IOException {
        this.extensionId = PKIXExtensions.ExtendedKeyUsage_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for ExtendedKeyUsageExtension.");
        }
        this.keyUsages = new Vector<ObjectIdentifier>();
        while (derValue.data.available() != 0) {
            this.keyUsages.addElement(derValue.data.getDerValue().getOID());
        }
    }
    
    @Override
    public String toString() {
        if (this.keyUsages == null) {
            return "";
        }
        String s = "  ";
        int n = 1;
        for (final ObjectIdentifier objectIdentifier : this.keyUsages) {
            if (n == 0) {
                s += "\n  ";
            }
            final String s2 = ExtendedKeyUsageExtension.map.get(objectIdentifier);
            if (s2 != null) {
                s += s2;
            }
            else {
                s += objectIdentifier.toString();
            }
            n = 0;
        }
        return super.toString() + "ExtendedKeyUsages [\n" + s + "\n]\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.ExtendedKeyUsage_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!s.equalsIgnoreCase("usages")) {
            throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:ExtendedKeyUsageExtension.");
        }
        if (!(o instanceof Vector)) {
            throw new IOException("Attribute value should be of type Vector.");
        }
        this.keyUsages = (Vector)o;
        this.encodeThis();
    }
    
    @Override
    public Vector<ObjectIdentifier> get(final String s) throws IOException {
        if (s.equalsIgnoreCase("usages")) {
            return this.keyUsages;
        }
        throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:ExtendedKeyUsageExtension.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("usages")) {
            this.keyUsages = null;
            this.encodeThis();
            return;
        }
        throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:ExtendedKeyUsageExtension.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("usages");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "ExtendedKeyUsage";
    }
    
    public List<String> getExtendedKeyUsage() {
        final ArrayList list = new ArrayList(this.keyUsages.size());
        final Iterator<ObjectIdentifier> iterator = this.keyUsages.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().toString());
        }
        return list;
    }
    
    static {
        map = new HashMap<ObjectIdentifier, String>();
        anyExtendedKeyUsageOidData = new int[] { 2, 5, 29, 37, 0 };
        serverAuthOidData = new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 1 };
        clientAuthOidData = new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 2 };
        codeSigningOidData = new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 3 };
        emailProtectionOidData = new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 4 };
        ipsecEndSystemOidData = new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 5 };
        ipsecTunnelOidData = new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 6 };
        ipsecUserOidData = new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 7 };
        timeStampingOidData = new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 8 };
        OCSPSigningOidData = new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 9 };
        ExtendedKeyUsageExtension.map.put(ObjectIdentifier.newInternal(ExtendedKeyUsageExtension.anyExtendedKeyUsageOidData), "anyExtendedKeyUsage");
        ExtendedKeyUsageExtension.map.put(ObjectIdentifier.newInternal(ExtendedKeyUsageExtension.serverAuthOidData), "serverAuth");
        ExtendedKeyUsageExtension.map.put(ObjectIdentifier.newInternal(ExtendedKeyUsageExtension.clientAuthOidData), "clientAuth");
        ExtendedKeyUsageExtension.map.put(ObjectIdentifier.newInternal(ExtendedKeyUsageExtension.codeSigningOidData), "codeSigning");
        ExtendedKeyUsageExtension.map.put(ObjectIdentifier.newInternal(ExtendedKeyUsageExtension.emailProtectionOidData), "emailProtection");
        ExtendedKeyUsageExtension.map.put(ObjectIdentifier.newInternal(ExtendedKeyUsageExtension.ipsecEndSystemOidData), "ipsecEndSystem");
        ExtendedKeyUsageExtension.map.put(ObjectIdentifier.newInternal(ExtendedKeyUsageExtension.ipsecTunnelOidData), "ipsecTunnel");
        ExtendedKeyUsageExtension.map.put(ObjectIdentifier.newInternal(ExtendedKeyUsageExtension.ipsecUserOidData), "ipsecUser");
        ExtendedKeyUsageExtension.map.put(ObjectIdentifier.newInternal(ExtendedKeyUsageExtension.timeStampingOidData), "timeStamping");
        ExtendedKeyUsageExtension.map.put(ObjectIdentifier.newInternal(ExtendedKeyUsageExtension.OCSPSigningOidData), "OCSPSigning");
    }
}
