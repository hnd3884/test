package com.maverick.crypto.asn1;

import java.io.IOException;

public class DERPrintableString extends DERObject implements DERString
{
    String ob;
    
    public static DERPrintableString getInstance(final Object o) {
        if (o == null || o instanceof DERPrintableString) {
            return (DERPrintableString)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERPrintableString(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERPrintableString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERPrintableString(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        this.ob = new String(array2);
    }
    
    public DERPrintableString(final String ob) {
        this.ob = ob;
    }
    
    public String getString() {
        return this.ob;
    }
    
    public byte[] getOctets() {
        final char[] charArray = this.ob.toCharArray();
        final byte[] array = new byte[charArray.length];
        for (int i = 0; i != charArray.length; ++i) {
            array[i] = (byte)charArray[i];
        }
        return array;
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(19, this.getOctets());
    }
    
    public int hashCode() {
        return this.getString().hashCode();
    }
    
    public boolean equals(final Object o) {
        return o instanceof DERPrintableString && this.getString().equals(((DERPrintableString)o).getString());
    }
}
