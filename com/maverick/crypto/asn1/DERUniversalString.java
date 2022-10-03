package com.maverick.crypto.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DERUniversalString extends DERObject implements DERString
{
    private static final char[] lb;
    private byte[] kb;
    
    public static DERUniversalString getInstance(final Object o) {
        if (o == null || o instanceof DERUniversalString) {
            return (DERUniversalString)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERUniversalString(((ASN1OctetString)o).getOctets());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERUniversalString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERUniversalString(final byte[] kb) {
        this.kb = kb;
    }
    
    public String getString() {
        final StringBuffer sb = new StringBuffer("#");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ASN1OutputStream asn1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
        try {
            asn1OutputStream.writeObject(this);
        }
        catch (final IOException ex) {
            throw new RuntimeException("internal error encoding BitString");
        }
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        for (int i = 0; i != byteArray.length; ++i) {
            sb.append(DERUniversalString.lb[(byteArray[i] >>> 4) % 15]);
            sb.append(DERUniversalString.lb[byteArray[i] & 0xF]);
        }
        return sb.toString();
    }
    
    public byte[] getOctets() {
        return this.kb;
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(28, this.getOctets());
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERUniversalString && this.getString().equals(((DERUniversalString)o).getString());
    }
    
    static {
        lb = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
