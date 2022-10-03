package org.bouncycastle.asn1;

import java.io.IOException;

public abstract class ASN1Null extends ASN1Primitive
{
    public static ASN1Null getInstance(final Object o) {
        if (o instanceof ASN1Null) {
            return (ASN1Null)o;
        }
        if (o != null) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("failed to construct NULL from byte[]: " + ex.getMessage());
            }
            catch (final ClassCastException ex2) {
                throw new IllegalArgumentException("unknown object in getInstance(): " + o.getClass().getName());
            }
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        return -1;
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof ASN1Null;
    }
    
    @Override
    abstract void encode(final ASN1OutputStream p0) throws IOException;
    
    @Override
    public String toString() {
        return "NULL";
    }
}
