package com.maverick.crypto.asn1;

import java.io.IOException;

public class DERBMPString extends DERObject implements DERString
{
    String nb;
    
    public static DERBMPString getInstance(final Object o) {
        if (o == null || o instanceof DERBMPString) {
            return (DERBMPString)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERBMPString(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERBMPString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERBMPString(final byte[] array) {
        final char[] array2 = new char[array.length / 2];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[2 * i] << 8 | (array[2 * i + 1] & 0xFF));
        }
        this.nb = new String(array2);
    }
    
    public DERBMPString(final String nb) {
        this.nb = nb;
    }
    
    public String getString() {
        return this.nb;
    }
    
    public int hashCode() {
        return this.getString().hashCode();
    }
    
    public boolean equals(final Object o) {
        return o instanceof DERBMPString && this.getString().equals(((DERBMPString)o).getString());
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        final char[] charArray = this.nb.toCharArray();
        final byte[] array = new byte[charArray.length * 2];
        for (int i = 0; i != charArray.length; ++i) {
            array[2 * i] = (byte)(charArray[i] >> 8);
            array[2 * i + 1] = (byte)charArray[i];
        }
        derOutputStream.b(30, array);
    }
}
