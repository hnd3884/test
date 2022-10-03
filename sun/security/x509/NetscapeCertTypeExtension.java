package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.DerValue;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import java.util.Vector;
import sun.security.util.ObjectIdentifier;

public class NetscapeCertTypeExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.NetscapeCertType";
    public static final String NAME = "NetscapeCertType";
    public static final String SSL_CLIENT = "ssl_client";
    public static final String SSL_SERVER = "ssl_server";
    public static final String S_MIME = "s_mime";
    public static final String OBJECT_SIGNING = "object_signing";
    public static final String SSL_CA = "ssl_ca";
    public static final String S_MIME_CA = "s_mime_ca";
    public static final String OBJECT_SIGNING_CA = "object_signing_ca";
    private static final int[] CertType_data;
    public static ObjectIdentifier NetscapeCertType_Id;
    private boolean[] bitString;
    private static MapEntry[] mMapData;
    private static final Vector<String> mAttributeNames;
    
    private static int getPosition(final String s) throws IOException {
        for (int i = 0; i < NetscapeCertTypeExtension.mMapData.length; ++i) {
            if (s.equalsIgnoreCase(NetscapeCertTypeExtension.mMapData[i].mName)) {
                return NetscapeCertTypeExtension.mMapData[i].mPosition;
            }
        }
        throw new IOException("Attribute name [" + s + "] not recognized by CertAttrSet:NetscapeCertType.");
    }
    
    private void encodeThis() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putTruncatedUnalignedBitString(new BitArray(this.bitString));
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    private boolean isSet(final int n) {
        return n < this.bitString.length && this.bitString[n];
    }
    
    private void set(final int n, final boolean b) {
        if (n >= this.bitString.length) {
            final boolean[] bitString = new boolean[n + 1];
            System.arraycopy(this.bitString, 0, bitString, 0, this.bitString.length);
            this.bitString = bitString;
        }
        this.bitString[n] = b;
    }
    
    public NetscapeCertTypeExtension(final byte[] array) throws IOException {
        this.bitString = new BitArray(array.length * 8, array).toBooleanArray();
        this.extensionId = NetscapeCertTypeExtension.NetscapeCertType_Id;
        this.critical = true;
        this.encodeThis();
    }
    
    public NetscapeCertTypeExtension(final boolean[] bitString) throws IOException {
        this.bitString = bitString;
        this.extensionId = NetscapeCertTypeExtension.NetscapeCertType_Id;
        this.critical = true;
        this.encodeThis();
    }
    
    public NetscapeCertTypeExtension(final Boolean b, final Object o) throws IOException {
        this.extensionId = NetscapeCertTypeExtension.NetscapeCertType_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        this.bitString = new DerValue(this.extensionValue).getUnalignedBitString().toBooleanArray();
    }
    
    public NetscapeCertTypeExtension() {
        this.extensionId = NetscapeCertTypeExtension.NetscapeCertType_Id;
        this.critical = true;
        this.bitString = new boolean[0];
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof Boolean)) {
            throw new IOException("Attribute must be of type Boolean.");
        }
        this.set(getPosition(s), (boolean)o);
        this.encodeThis();
    }
    
    @Override
    public Boolean get(final String s) throws IOException {
        return this.isSet(getPosition(s));
    }
    
    @Override
    public void delete(final String s) throws IOException {
        this.set(getPosition(s), false);
        this.encodeThis();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("NetscapeCertType [\n");
        if (this.isSet(0)) {
            sb.append("   SSL client\n");
        }
        if (this.isSet(1)) {
            sb.append("   SSL server\n");
        }
        if (this.isSet(2)) {
            sb.append("   S/MIME\n");
        }
        if (this.isSet(3)) {
            sb.append("   Object Signing\n");
        }
        if (this.isSet(5)) {
            sb.append("   SSL CA\n");
        }
        if (this.isSet(6)) {
            sb.append("   S/MIME CA\n");
        }
        if (this.isSet(7)) {
            sb.append("   Object Signing CA");
        }
        sb.append("]\n");
        return sb.toString();
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = NetscapeCertTypeExtension.NetscapeCertType_Id;
            this.critical = true;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public Enumeration<String> getElements() {
        return NetscapeCertTypeExtension.mAttributeNames.elements();
    }
    
    @Override
    public String getName() {
        return "NetscapeCertType";
    }
    
    public boolean[] getKeyUsageMappedBits() {
        final KeyUsageExtension keyUsageExtension = new KeyUsageExtension();
        final Boolean true = Boolean.TRUE;
        try {
            if (this.isSet(getPosition("ssl_client")) || this.isSet(getPosition("s_mime")) || this.isSet(getPosition("object_signing"))) {
                keyUsageExtension.set("digital_signature", true);
            }
            if (this.isSet(getPosition("ssl_server"))) {
                keyUsageExtension.set("key_encipherment", true);
            }
            if (this.isSet(getPosition("ssl_ca")) || this.isSet(getPosition("s_mime_ca")) || this.isSet(getPosition("object_signing_ca"))) {
                keyUsageExtension.set("key_certsign", true);
            }
        }
        catch (final IOException ex) {}
        return keyUsageExtension.getBits();
    }
    
    static {
        CertType_data = new int[] { 2, 16, 840, 1, 113730, 1, 1 };
        try {
            NetscapeCertTypeExtension.NetscapeCertType_Id = new ObjectIdentifier(NetscapeCertTypeExtension.CertType_data);
        }
        catch (final IOException ex) {}
        NetscapeCertTypeExtension.mMapData = new MapEntry[] { new MapEntry("ssl_client", 0), new MapEntry("ssl_server", 1), new MapEntry("s_mime", 2), new MapEntry("object_signing", 3), new MapEntry("ssl_ca", 5), new MapEntry("s_mime_ca", 6), new MapEntry("object_signing_ca", 7) };
        mAttributeNames = new Vector<String>();
        final MapEntry[] mMapData = NetscapeCertTypeExtension.mMapData;
        for (int length = mMapData.length, i = 0; i < length; ++i) {
            NetscapeCertTypeExtension.mAttributeNames.add(mMapData[i].mName);
        }
    }
    
    private static class MapEntry
    {
        String mName;
        int mPosition;
        
        MapEntry(final String mName, final int mPosition) {
            this.mName = mName;
            this.mPosition = mPosition;
        }
    }
}
