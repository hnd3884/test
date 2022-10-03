package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public class DERBMPString extends ASN1Primitive implements ASN1String
{
    private final char[] string;
    
    public static DERBMPString getInstance(final Object o) {
        if (o == null || o instanceof DERBMPString) {
            return (DERBMPString)o;
        }
        if (o instanceof byte[]) {
            try {
                return (DERBMPString)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERBMPString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERBMPString) {
            return getInstance(object);
        }
        return new DERBMPString(ASN1OctetString.getInstance(object).getOctets());
    }
    
    DERBMPString(final byte[] array) {
        final char[] string = new char[array.length / 2];
        for (int i = 0; i != string.length; ++i) {
            string[i] = (char)(array[2 * i] << 8 | (array[2 * i + 1] & 0xFF));
        }
        this.string = string;
    }
    
    DERBMPString(final char[] string) {
        this.string = string;
    }
    
    public DERBMPString(final String s) {
        this.string = s.toCharArray();
    }
    
    public String getString() {
        return new String(this.string);
    }
    
    @Override
    public String toString() {
        return this.getString();
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
    
    protected boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof DERBMPString && Arrays.areEqual(this.string, ((DERBMPString)asn1Primitive).string);
    }
    
    @Override
    boolean isConstructed() {
        return false;
    }
    
    @Override
    int encodedLength() {
        return 1 + StreamUtil.calculateBodyLength(this.string.length * 2) + this.string.length * 2;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.write(30);
        asn1OutputStream.writeLength(this.string.length * 2);
        for (int i = 0; i != this.string.length; ++i) {
            final char c = this.string[i];
            asn1OutputStream.write((byte)(c >> 8));
            asn1OutputStream.write((byte)c);
        }
    }
}
