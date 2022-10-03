package com.maverick.crypto.asn1;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class DERUTF8String extends DERObject implements DERString
{
    String hb;
    
    public static DERUTF8String getInstance(final Object o) {
        if (o == null || o instanceof DERUTF8String) {
            return (DERUTF8String)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERUTF8String(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERUTF8String getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    DERUTF8String(final byte[] array) {
        int i = 0;
        int n = 0;
        while (i < array.length) {
            ++n;
            if ((array[i] & 0xE0) == 0xE0) {
                i += 3;
            }
            else if ((array[i] & 0xC0) == 0xC0) {
                i += 2;
            }
            else {
                ++i;
            }
        }
        final char[] array2 = new char[n];
        int j = 0;
        int n2 = 0;
        while (j < array.length) {
            char c;
            if ((array[j] & 0xE0) == 0xE0) {
                c = (char)((array[j] & 0x1F) << 12 | (array[j + 1] & 0x3F) << 6 | (array[j + 2] & 0x3F));
                j += 3;
            }
            else if ((array[j] & 0xC0) == 0xC0) {
                c = (char)((array[j] & 0x3F) << 6 | (array[j + 1] & 0x3F));
                j += 2;
            }
            else {
                c = (char)(array[j] & 0xFF);
                ++j;
            }
            array2[n2++] = c;
        }
        this.hb = new String(array2);
    }
    
    public DERUTF8String(final String hb) {
        this.hb = hb;
    }
    
    public String getString() {
        return this.hb;
    }
    
    public int hashCode() {
        return this.getString().hashCode();
    }
    
    public boolean equals(final Object o) {
        return o instanceof DERUTF8String && this.getString().equals(((DERUTF8String)o).getString());
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        final char[] charArray = this.hb.toCharArray();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != charArray.length; ++i) {
            final char c = charArray[i];
            if (c < '\u0080') {
                byteArrayOutputStream.write(c);
            }
            else if (c < '\u0800') {
                byteArrayOutputStream.write(0xC0 | c >> 6);
                byteArrayOutputStream.write(0x80 | (c & '?'));
            }
            else {
                byteArrayOutputStream.write(0xE0 | c >> 12);
                byteArrayOutputStream.write(0x80 | (c >> 6 & 0x3F));
                byteArrayOutputStream.write(0x80 | (c & '?'));
            }
        }
        derOutputStream.b(12, byteArrayOutputStream.toByteArray());
    }
}
