package com.maverick.crypto.asn1;

import java.io.IOException;
import java.math.BigInteger;

public class DERInteger extends DERObject
{
    byte[] vb;
    
    public static DERInteger getInstance(final Object o) {
        if (o == null || o instanceof DERInteger) {
            return (DERInteger)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERInteger(((ASN1OctetString)o).getOctets());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERInteger getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERInteger(final int n) {
        this.vb = BigInteger.valueOf(n).toByteArray();
    }
    
    public DERInteger(final BigInteger bigInteger) {
        this.vb = bigInteger.toByteArray();
    }
    
    public DERInteger(final byte[] vb) {
        this.vb = vb;
    }
    
    public BigInteger getValue() {
        return new BigInteger(this.vb);
    }
    
    public BigInteger getPositiveValue() {
        return new BigInteger(1, this.vb);
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(2, this.vb);
    }
    
    public int hashCode() {
        int n = 0;
        for (int i = 0; i != this.vb.length; ++i) {
            n ^= (this.vb[i] & 0xFF) << i % 4;
        }
        return n;
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof DERInteger)) {
            return false;
        }
        final DERInteger derInteger = (DERInteger)o;
        if (this.vb.length != derInteger.vb.length) {
            return false;
        }
        for (int i = 0; i != this.vb.length; ++i) {
            if (this.vb[i] != derInteger.vb[i]) {
                return false;
            }
        }
        return true;
    }
}
