package jcifs.spnego.asn1;

import java.io.IOException;

public class DERPrintableString extends DERObject implements DERString
{
    String string;
    
    public static DERPrintableString getInstance(final Object obj) {
        if (obj == null || obj instanceof DERPrintableString) {
            return (DERPrintableString)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERPrintableString(((ASN1OctetString)obj).getOctets());
        }
        if (obj instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERPrintableString getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    public DERPrintableString(final byte[] string) {
        final char[] cs = new char[string.length];
        for (int i = 0; i != cs.length; ++i) {
            cs[i] = (char)(string[i] & 0xFF);
        }
        this.string = new String(cs);
    }
    
    public DERPrintableString(final String string) {
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
        out.writeEncoded(19, this.getOctets());
    }
    
    public int hashCode() {
        return this.getString().hashCode();
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof DERPrintableString)) {
            return false;
        }
        final DERPrintableString s = (DERPrintableString)o;
        return this.getString().equals(s.getString());
    }
}
