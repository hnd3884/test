package jcifs.spnego.asn1;

import java.io.IOException;

public class DERBMPString extends DERObject implements DERString
{
    String string;
    
    public static DERBMPString getInstance(final Object obj) {
        if (obj == null || obj instanceof DERBMPString) {
            return (DERBMPString)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERBMPString(((ASN1OctetString)obj).getOctets());
        }
        if (obj instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERBMPString getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    public DERBMPString(final byte[] string) {
        final char[] cs = new char[string.length / 2];
        for (int i = 0; i != cs.length; ++i) {
            cs[i] = (char)(string[2 * i] << 8 | (string[2 * i + 1] & 0xFF));
        }
        this.string = new String(cs);
    }
    
    public DERBMPString(final String string) {
        this.string = string;
    }
    
    public String getString() {
        return this.string;
    }
    
    public int hashCode() {
        return this.getString().hashCode();
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof DERBMPString)) {
            return false;
        }
        final DERPrintableString s = (DERPrintableString)o;
        return this.getString().equals(s.getString());
    }
    
    void encode(final DEROutputStream out) throws IOException {
        final char[] c = this.string.toCharArray();
        final byte[] b = new byte[c.length * 2];
        for (int i = 0; i != c.length; ++i) {
            b[2 * i] = (byte)(c[i] >> 8);
            b[2 * i + 1] = (byte)c[i];
        }
        out.writeEncoded(30, b);
    }
}
