package org.bouncycastle.asn1;

import org.bouncycastle.util.Strings;
import java.io.IOException;
import org.bouncycastle.util.Arrays;

public class DERVideotexString extends ASN1Primitive implements ASN1String
{
    private final byte[] string;
    
    public static DERVideotexString getInstance(final Object o) {
        if (o == null || o instanceof DERVideotexString) {
            return (DERVideotexString)o;
        }
        if (o instanceof byte[]) {
            try {
                return (DERVideotexString)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERVideotexString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERVideotexString) {
            return getInstance(object);
        }
        return new DERVideotexString(((ASN1OctetString)object).getOctets());
    }
    
    public DERVideotexString(final byte[] array) {
        this.string = Arrays.clone(array);
    }
    
    public byte[] getOctets() {
        return Arrays.clone(this.string);
    }
    
    @Override
    boolean isConstructed() {
        return false;
    }
    
    @Override
    int encodedLength() {
        return 1 + StreamUtil.calculateBodyLength(this.string.length) + this.string.length;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.writeEncoded(21, this.string);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof DERVideotexString && Arrays.areEqual(this.string, ((DERVideotexString)asn1Primitive).string);
    }
    
    public String getString() {
        return Strings.fromByteArray(this.string);
    }
}
