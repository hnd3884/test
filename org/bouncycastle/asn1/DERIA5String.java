package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERIA5String extends ASN1Primitive implements ASN1String
{
    private final byte[] string;
    
    public static DERIA5String getInstance(final Object o) {
        if (o == null || o instanceof DERIA5String) {
            return (DERIA5String)o;
        }
        if (o instanceof byte[]) {
            try {
                return (DERIA5String)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERIA5String getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERIA5String) {
            return getInstance(object);
        }
        return new DERIA5String(((ASN1OctetString)object).getOctets());
    }
    
    DERIA5String(final byte[] string) {
        this.string = string;
    }
    
    public DERIA5String(final String s) {
        this(s, false);
    }
    
    public DERIA5String(final String s, final boolean b) {
        if (s == null) {
            throw new NullPointerException("string cannot be null");
        }
        if (b && !isIA5String(s)) {
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
        asn1OutputStream.writeEncoded(22, this.string);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof DERIA5String && Arrays.areEqual(this.string, ((DERIA5String)asn1Primitive).string);
    }
    
    public static boolean isIA5String(final String s) {
        for (int i = s.length() - 1; i >= 0; --i) {
            if (s.charAt(i) > '\u007f') {
                return false;
            }
        }
        return true;
    }
}
