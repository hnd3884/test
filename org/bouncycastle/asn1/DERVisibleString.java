package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERVisibleString extends ASN1Primitive implements ASN1String
{
    private final byte[] string;
    
    public static DERVisibleString getInstance(final Object o) {
        if (o == null || o instanceof DERVisibleString) {
            return (DERVisibleString)o;
        }
        if (o instanceof byte[]) {
            try {
                return (DERVisibleString)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERVisibleString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERVisibleString) {
            return getInstance(object);
        }
        return new DERVisibleString(ASN1OctetString.getInstance(object).getOctets());
    }
    
    DERVisibleString(final byte[] string) {
        this.string = string;
    }
    
    public DERVisibleString(final String s) {
        this.string = Strings.toByteArray(s);
    }
    
    public String getString() {
        return Strings.fromByteArray(this.string);
    }
    
    @Override
    public String toString() {
        return this.getString();
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
        asn1OutputStream.writeEncoded(26, this.string);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof DERVisibleString && Arrays.areEqual(this.string, ((DERVisibleString)asn1Primitive).string);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
}
