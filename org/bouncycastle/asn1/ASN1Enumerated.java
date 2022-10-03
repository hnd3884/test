package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;
import java.math.BigInteger;

public class ASN1Enumerated extends ASN1Primitive
{
    private final byte[] bytes;
    private static ASN1Enumerated[] cache;
    
    public static ASN1Enumerated getInstance(final Object o) {
        if (o == null || o instanceof ASN1Enumerated) {
            return (ASN1Enumerated)o;
        }
        if (o instanceof byte[]) {
            try {
                return (ASN1Enumerated)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static ASN1Enumerated getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof ASN1Enumerated) {
            return getInstance(object);
        }
        return fromOctetString(((ASN1OctetString)object).getOctets());
    }
    
    public ASN1Enumerated(final int n) {
        this.bytes = BigInteger.valueOf(n).toByteArray();
    }
    
    public ASN1Enumerated(final BigInteger bigInteger) {
        this.bytes = bigInteger.toByteArray();
    }
    
    public ASN1Enumerated(final byte[] array) {
        if (!Properties.isOverrideSet("org.bouncycastle.asn1.allow_unsafe_integer") && ASN1Integer.isMalformed(array)) {
            throw new IllegalArgumentException("malformed enumerated");
        }
        this.bytes = Arrays.clone(array);
    }
    
    public BigInteger getValue() {
        return new BigInteger(this.bytes);
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
        asn1OutputStream.writeEncoded(10, this.bytes);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof ASN1Enumerated && Arrays.areEqual(this.bytes, ((ASN1Enumerated)asn1Primitive).bytes);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }
    
    static ASN1Enumerated fromOctetString(final byte[] array) {
        if (array.length > 1) {
            return new ASN1Enumerated(array);
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("ENUMERATED has zero length");
        }
        final int n = array[0] & 0xFF;
        if (n >= ASN1Enumerated.cache.length) {
            return new ASN1Enumerated(Arrays.clone(array));
        }
        ASN1Enumerated asn1Enumerated = ASN1Enumerated.cache[n];
        if (asn1Enumerated == null) {
            final ASN1Enumerated[] cache = ASN1Enumerated.cache;
            final int n2 = n;
            final ASN1Enumerated asn1Enumerated2 = new ASN1Enumerated(Arrays.clone(array));
            cache[n2] = asn1Enumerated2;
            asn1Enumerated = asn1Enumerated2;
        }
        return asn1Enumerated;
    }
    
    static {
        ASN1Enumerated.cache = new ASN1Enumerated[12];
    }
}
