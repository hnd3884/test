package com.maverick.crypto.asn1;

import java.io.IOException;

public class DERT61String extends DERObject implements DERString
{
    String ib;
    
    public static DERT61String getInstance(final Object o) {
        if (o == null || o instanceof DERT61String) {
            return (DERT61String)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERT61String(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERT61String getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERT61String(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        this.ib = new String(array2);
    }
    
    public DERT61String(final String ib) {
        this.ib = ib;
    }
    
    public String getString() {
        return this.ib;
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(20, this.getOctets());
    }
    
    public byte[] getOctets() {
        final char[] charArray = this.ib.toCharArray();
        final byte[] array = new byte[charArray.length];
        for (int i = 0; i != charArray.length; ++i) {
            array[i] = (byte)charArray[i];
        }
        return array;
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERT61String && this.getString().equals(((DERT61String)o).getString());
    }
}
