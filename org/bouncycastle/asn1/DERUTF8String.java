package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERUTF8String extends ASN1Primitive implements ASN1String
{
    private final byte[] string;
    
    public static DERUTF8String getInstance(final Object o) {
        if (o == null || o instanceof DERUTF8String) {
            return (DERUTF8String)o;
        }
        if (o instanceof byte[]) {
            try {
                return (DERUTF8String)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERUTF8String getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERUTF8String) {
            return getInstance(object);
        }
        return new DERUTF8String(ASN1OctetString.getInstance(object).getOctets());
    }
    
    DERUTF8String(final byte[] string) {
        this.string = string;
    }
    
    public DERUTF8String(final String s) {
        this.string = Strings.toUTF8ByteArray(s);
    }
    
    public String getString() {
        return Strings.fromUTF8ByteArray(this.string);
    }
    
    @Override
    public String toString() {
        return this.getString();
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof DERUTF8String && Arrays.areEqual(this.string, ((DERUTF8String)asn1Primitive).string);
    }
    
    @Override
    boolean isConstructed() {
        return false;
    }
    
    @Override
    int encodedLength() throws IOException {
        return 1 + StreamUtil.calculateBodyLength(this.string.length) + this.string.length;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.writeEncoded(12, this.string);
    }
}
