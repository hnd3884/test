package jcifs.spnego.asn1;

import java.io.IOException;

public class DERVisibleString extends DERObject implements DERString
{
    String string;
    
    public static DERVisibleString getInstance(final Object obj) {
        if (obj == null || obj instanceof DERVisibleString) {
            return (DERVisibleString)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERVisibleString(((ASN1OctetString)obj).getOctets());
        }
        if (obj instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERVisibleString getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    public DERVisibleString(final byte[] string) {
        final char[] cs = new char[string.length];
        for (int i = 0; i != cs.length; ++i) {
            cs[i] = (char)(string[i] & 0xFF);
        }
        this.string = new String(cs);
    }
    
    public DERVisibleString(final String string) {
        this.string = string;
    }
    
    public String getString() {
        return this.string;
    }
    
    public byte[] getOctets() {
        final char[] cs = this.string.toCharArray();
        final byte[] bs = new byte[cs.length];
        for (int i = 0; i != cs.length; ++i) {
            bs[i] = (byte)cs[i];
        }
        return bs;
    }
    
    void encode(final DEROutputStream out) throws IOException {
        out.writeEncoded(26, this.getOctets());
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERVisibleString && this.getString().equals(((DERVisibleString)o).getString());
    }
}
