package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;
import java.math.BigInteger;

public class ASN1Integer extends ASN1Primitive
{
    private final byte[] bytes;
    
    public static ASN1Integer getInstance(final Object o) {
        if (o == null || o instanceof ASN1Integer) {
            return (ASN1Integer)o;
        }
        if (o instanceof byte[]) {
            try {
                return (ASN1Integer)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static ASN1Integer getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof ASN1Integer) {
            return getInstance(object);
        }
        return new ASN1Integer(ASN1OctetString.getInstance(object).getOctets());
    }
    
    public ASN1Integer(final long n) {
        this.bytes = BigInteger.valueOf(n).toByteArray();
    }
    
    public ASN1Integer(final BigInteger bigInteger) {
        this.bytes = bigInteger.toByteArray();
    }
    
    public ASN1Integer(final byte[] array) {
        this(array, true);
    }
    
    ASN1Integer(final byte[] array, final boolean b) {
        if (!Properties.isOverrideSet("org.bouncycastle.asn1.allow_unsafe_integer") && isMalformed(array)) {
            throw new IllegalArgumentException("malformed integer");
        }
        this.bytes = (b ? Arrays.clone(array) : array);
    }
    
    static boolean isMalformed(final byte[] array) {
        if (array.length > 1) {
            if (array[0] == 0 && (array[1] & 0x80) == 0x0) {
                return true;
            }
            if (array[0] == -1 && (array[1] & 0x80) != 0x0) {
                return true;
            }
        }
        return false;
    }
    
    public BigInteger getValue() {
        return new BigInteger(this.bytes);
    }
    
    public BigInteger getPositiveValue() {
        return new BigInteger(1, this.bytes);
    }
    
    @Override
    boolean isConstructed() {
        return false;
    }
    
    @Override
    int encodedLength() {
        return 1 + StreamUtil.calculateBodyLength(this.bytes.length) + this.bytes.length;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.writeEncoded(2, this.bytes);
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (int i = 0; i != this.bytes.length; ++i) {
            n ^= (this.bytes[i] & 0xFF) << i % 4;
        }
        return n;
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof ASN1Integer && Arrays.areEqual(this.bytes, ((ASN1Integer)asn1Primitive).bytes);
    }
    
    @Override
    public String toString() {
        return this.getValue().toString();
    }
}
