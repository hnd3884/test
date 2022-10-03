package jcifs.spnego.asn1;

import java.io.IOException;
import java.math.BigInteger;

public class DERInteger extends DERObject
{
    byte[] bytes;
    
    public static DERInteger getInstance(final Object obj) {
        if (obj == null || obj instanceof DERInteger) {
            return (DERInteger)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERInteger(((ASN1OctetString)obj).getOctets());
        }
        if (obj instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERInteger getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    public DERInteger(final int value) {
        this.bytes = BigInteger.valueOf(value).toByteArray();
    }
    
    public DERInteger(final BigInteger value) {
        this.bytes = value.toByteArray();
    }
    
    public DERInteger(final byte[] bytes) {
        this.bytes = bytes;
    }
    
    public BigInteger getValue() {
        return new BigInteger(this.bytes);
    }
    
    public BigInteger getPositiveValue() {
        return new BigInteger(1, this.bytes);
    }
    
    void encode(final DEROutputStream out) throws IOException {
        out.writeEncoded(2, this.bytes);
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof DERInteger)) {
            return false;
        }
        final DERInteger other = (DERInteger)o;
        if (this.bytes.length != other.bytes.length) {
            return false;
        }
        for (int i = 0; i != this.bytes.length; ++i) {
            if (this.bytes[i] != other.bytes[i]) {
                return false;
            }
        }
        return true;
    }
}
