package com.maverick.crypto.asn1;

import java.io.IOException;

public class DERBoolean extends DERObject
{
    byte hc;
    public static final DERBoolean FALSE;
    public static final DERBoolean TRUE;
    
    public static DERBoolean getInstance(final Object o) {
        if (o == null || o instanceof DERBoolean) {
            return (DERBoolean)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERBoolean(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERBoolean getInstance(final boolean b) {
        return b ? DERBoolean.TRUE : DERBoolean.FALSE;
    }
    
    public static DERBoolean getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERBoolean(final byte[] array) {
        this.hc = array[0];
    }
    
    public DERBoolean(final boolean b) {
        this.hc = (byte)(b ? -1 : 0);
    }
    
    public boolean isTrue() {
        return this.hc != 0;
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(1, new byte[] { this.hc });
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERBoolean && this.hc == ((DERBoolean)o).hc;
    }
    
    static {
        FALSE = new DERBoolean(false);
        TRUE = new DERBoolean(true);
    }
}
