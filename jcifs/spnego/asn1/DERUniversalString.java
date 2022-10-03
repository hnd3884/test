package jcifs.spnego.asn1;

import java.io.IOException;

public class DERUniversalString extends DERObject implements DERString
{
    byte[] string;
    char[] table;
    
    public static DERUniversalString getInstance(final Object obj) {
        if (obj == null || obj instanceof DERUniversalString) {
            return (DERUniversalString)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERUniversalString(((ASN1OctetString)obj).getOctets());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERUniversalString getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    public DERUniversalString(final byte[] string) {
        this.table = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        this.string = string;
    }
    
    public String getString() {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i != this.string.length; ++i) {
            buf.append(this.table[(this.string[i] >>> 4) % 15]);
            buf.append(this.table[this.string[i] & 0xF]);
        }
        return buf.toString();
    }
    
    public byte[] getOctets() {
        return this.string;
    }
    
    void encode(final DEROutputStream out) throws IOException {
        out.writeEncoded(28, this.getOctets());
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERUniversalString && this.getString().equals(((DERUniversalString)o).getString());
    }
}
