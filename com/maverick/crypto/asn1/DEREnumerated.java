package com.maverick.crypto.asn1;

import java.io.IOException;
import java.math.BigInteger;

public class DEREnumerated extends DERObject
{
    byte[] xb;
    
    public static DEREnumerated getInstance(final Object o) {
        if (o == null || o instanceof DEREnumerated) {
            return (DEREnumerated)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DEREnumerated(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DEREnumerated getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DEREnumerated(final int n) {
        this.xb = BigInteger.valueOf(n).toByteArray();
    }
    
    public DEREnumerated(final BigInteger bigInteger) {
        this.xb = bigInteger.toByteArray();
    }
    
    public DEREnumerated(final byte[] xb) {
        this.xb = xb;
    }
    
    public BigInteger getValue() {
        return new BigInteger(this.xb);
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(10, this.xb);
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof DEREnumerated)) {
            return false;
        }
        final DEREnumerated derEnumerated = (DEREnumerated)o;
        if (this.xb.length != derEnumerated.xb.length) {
            return false;
        }
        for (int i = 0; i != this.xb.length; ++i) {
            if (this.xb[i] != derEnumerated.xb[i]) {
                return false;
            }
        }
        return true;
    }
}
