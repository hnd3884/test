package com.maverick.crypto.asn1;

import java.io.IOException;

public class DERVisibleString extends DERObject implements DERString
{
    String fb;
    
    public static DERVisibleString getInstance(final Object o) {
        if (o == null || o instanceof DERVisibleString) {
            return (DERVisibleString)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERVisibleString(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERVisibleString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERVisibleString(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        this.fb = new String(array2);
    }
    
    public DERVisibleString(final String fb) {
        this.fb = fb;
    }
    
    public String getString() {
        return this.fb;
    }
    
    public byte[] getOctets() {
        final char[] charArray = this.fb.toCharArray();
        final byte[] array = new byte[charArray.length];
        for (int i = 0; i != charArray.length; ++i) {
            array[i] = (byte)charArray[i];
        }
        return array;
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(26, this.getOctets());
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERVisibleString && this.getString().equals(((DERVisibleString)o).getString());
    }
}
