package jcifs.spnego.asn1;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class DERUTF8String extends DERObject implements DERString
{
    String string;
    
    public static DERUTF8String getInstance(final Object obj) {
        if (obj == null || obj instanceof DERUTF8String) {
            return (DERUTF8String)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERUTF8String(((ASN1OctetString)obj).getOctets());
        }
        if (obj instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERUTF8String getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    DERUTF8String(final byte[] string) {
        int i = 0;
        int length = 0;
        while (i < string.length) {
            ++length;
            if ((string[i] & 0xE0) == 0xE0) {
                i += 3;
            }
            else if ((string[i] & 0xC0) == 0xC0) {
                i += 2;
            }
            else {
                ++i;
            }
        }
        final char[] cs = new char[length];
        i = 0;
        length = 0;
        while (i < string.length) {
            char ch;
            if ((string[i] & 0xE0) == 0xE0) {
                ch = (char)((string[i] & 0x1F) << 12 | (string[i + 1] & 0x3F) << 6 | (string[i + 2] & 0x3F));
                i += 3;
            }
            else if ((string[i] & 0xC0) == 0xC0) {
                ch = (char)((string[i] & 0x3F) << 6 | (string[i + 1] & 0x3F));
                i += 2;
            }
            else {
                ch = (char)(string[i] & 0xFF);
                ++i;
            }
            cs[length++] = ch;
        }
        this.string = new String(cs);
    }
    
    public DERUTF8String(final String string) {
        this.string = string;
    }
    
    public String getString() {
        return this.string;
    }
    
    public int hashCode() {
        return this.getString().hashCode();
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof DERUTF8String)) {
            return false;
        }
        final DERUTF8String s = (DERUTF8String)o;
        return this.getString().equals(s.getString());
    }
    
    void encode(final DEROutputStream out) throws IOException {
        final char[] c = this.string.toCharArray();
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != c.length; ++i) {
            final char ch = c[i];
            if (ch < '\u0080') {
                bOut.write(ch);
            }
            else if (ch < '\u0800') {
                bOut.write(0xC0 | ch >> 6);
                bOut.write(0x80 | (ch & '?'));
            }
            else {
                bOut.write(0xE0 | ch >> 12);
                bOut.write(0x80 | (ch >> 6 & 0x3F));
                bOut.write(0x80 | (ch & '?'));
            }
        }
        out.writeEncoded(12, bOut.toByteArray());
    }
}
