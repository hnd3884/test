package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERNumericString extends ASN1Primitive implements ASN1String
{
    private final byte[] string;
    
    public static DERNumericString getInstance(final Object o) {
        if (o == null || o instanceof DERNumericString) {
            return (DERNumericString)o;
        }
        if (o instanceof byte[]) {
            try {
                return (DERNumericString)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERNumericString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERNumericString) {
            return getInstance(object);
        }
        return new DERNumericString(ASN1OctetString.getInstance(object).getOctets());
    }
    
    DERNumericString(final byte[] string) {
        this.string = string;
    }
    
    public DERNumericString(final String s) {
        this(s, false);
    }
    
    public DERNumericString(final String s, final boolean b) {
        if (b && !isNumericString(s)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
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
        asn1OutputStream.writeEncoded(18, this.string);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof DERNumericString && Arrays.areEqual(this.string, ((DERNumericString)asn1Primitive).string);
    }
    
    public static boolean isNumericString(final String s) {
        for (int i = s.length() - 1; i >= 0; --i) {
            final char char1 = s.charAt(i);
            if (char1 > '\u007f') {
                return false;
            }
            if (('0' > char1 || char1 > '9') && char1 != ' ') {
                return false;
            }
        }
        return true;
    }
}
