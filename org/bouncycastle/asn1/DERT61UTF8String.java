package org.bouncycastle.asn1;

import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.util.Strings;

public class DERT61UTF8String extends ASN1Primitive implements ASN1String
{
    private byte[] string;
    
    public static DERT61UTF8String getInstance(final Object o) {
        if (o instanceof DERT61String) {
            return new DERT61UTF8String(((DERT61String)o).getOctets());
        }
        if (o == null || o instanceof DERT61UTF8String) {
            return (DERT61UTF8String)o;
        }
        if (o instanceof byte[]) {
            try {
                return new DERT61UTF8String(((DERT61String)ASN1Primitive.fromByteArray((byte[])o)).getOctets());
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERT61UTF8String getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERT61String || object instanceof DERT61UTF8String) {
            return getInstance(object);
        }
        return new DERT61UTF8String(ASN1OctetString.getInstance(object).getOctets());
    }
    
    public DERT61UTF8String(final byte[] string) {
        this.string = string;
    }
    
    public DERT61UTF8String(final String s) {
        this(Strings.toUTF8ByteArray(s));
    }
    
    public String getString() {
        return Strings.fromUTF8ByteArray(this.string);
    }
    
    @Override
    public String toString() {
        return this.getString();
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
        asn1OutputStream.writeEncoded(20, this.string);
    }
    
    public byte[] getOctets() {
        return Arrays.clone(this.string);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof DERT61UTF8String && Arrays.areEqual(this.string, ((DERT61UTF8String)asn1Primitive).string);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
}
