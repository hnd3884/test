package org.bouncycastle.asn1;

import org.bouncycastle.util.Strings;
import java.io.IOException;
import org.bouncycastle.util.Arrays;

public class DERGraphicString extends ASN1Primitive implements ASN1String
{
    private final byte[] string;
    
    public static DERGraphicString getInstance(final Object o) {
        if (o == null || o instanceof DERGraphicString) {
            return (DERGraphicString)o;
        }
        if (o instanceof byte[]) {
            try {
                return (DERGraphicString)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERGraphicString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERGraphicString) {
            return getInstance(object);
        }
        return new DERGraphicString(((ASN1OctetString)object).getOctets());
    }
    
    public DERGraphicString(final byte[] array) {
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
        asn1OutputStream.writeEncoded(25, this.string);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof DERGraphicString && Arrays.areEqual(this.string, ((DERGraphicString)asn1Primitive).string);
    }
    
    public String getString() {
        return Strings.fromByteArray(this.string);
    }
}
