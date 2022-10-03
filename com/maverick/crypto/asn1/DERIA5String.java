package com.maverick.crypto.asn1;

import java.io.IOException;

public class DERIA5String extends DERObject implements DERString
{
    String mb;
    
    public static DERIA5String getInstance(final Object o) {
        if (o == null || o instanceof DERIA5String) {
            return (DERIA5String)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERIA5String(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERIA5String getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERIA5String(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        this.mb = new String(array2);
    }
    
    public DERIA5String(final String mb) {
        this.mb = mb;
    }
    
    public String getString() {
        return this.mb;
    }
    
    public byte[] getOctets() {
        final char[] charArray = this.mb.toCharArray();
        final byte[] array = new byte[charArray.length];
        for (int i = 0; i != charArray.length; ++i) {
            array[i] = (byte)charArray[i];
        }
        return array;
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(22, this.getOctets());
    }
    
    public int hashCode() {
        return this.getString().hashCode();
    }
    
    public boolean equals(final Object o) {
        return o instanceof DERIA5String && this.getString().equals(((DERIA5String)o).getString());
    }
}
