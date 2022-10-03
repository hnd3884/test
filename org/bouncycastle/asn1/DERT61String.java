package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.Arrays;

public class DERT61String extends ASN1Primitive implements ASN1String
{
    private byte[] string;
    
    public static DERT61String getInstance(final Object o) {
        if (o == null || o instanceof DERT61String) {
            return (DERT61String)o;
        }
        if (o instanceof byte[]) {
            try {
                return (DERT61String)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERT61String getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERT61String) {
            return getInstance(object);
        }
        return new DERT61String(ASN1OctetString.getInstance(object).getOctets());
    }
    
    public DERT61String(final byte[] array) {
        this.string = Arrays.clone(array);
    }
    
    public DERT61String(final String s) {
        this.string = Strings.toByteArray(s);
    }
    
    public String getString() {
        return Strings.fromByteArray(this.string);
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
        return asn1Primitive instanceof DERT61String && Arrays.areEqual(this.string, ((DERT61String)asn1Primitive).string);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
}
