package com.maverick.crypto.asn1;

import java.io.IOException;

public class DERNumericString extends DERObject implements DERString
{
    String gb;
    
    public static DERNumericString getInstance(final Object o) {
        if (o == null || o instanceof DERNumericString) {
            return (DERNumericString)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERNumericString(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERNumericString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERNumericString(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        this.gb = new String(array2);
    }
    
    public DERNumericString(final String gb) {
        this.gb = gb;
    }
    
    public String getString() {
        return this.gb;
    }
    
    public byte[] getOctets() {
        final char[] charArray = this.gb.toCharArray();
        final byte[] array = new byte[charArray.length];
        for (int i = 0; i != charArray.length; ++i) {
            array[i] = (byte)charArray[i];
        }
        return array;
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(18, this.getOctets());
    }
    
    public int hashCode() {
        return this.getString().hashCode();
    }
    
    public boolean equals(final Object o) {
        return o instanceof DERNumericString && this.getString().equals(((DERNumericString)o).getString());
    }
}
