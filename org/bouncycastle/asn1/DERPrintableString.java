package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class DERPrintableString extends ASN1Primitive implements ASN1String
{
    private final byte[] string;
    
    public static DERPrintableString getInstance(final Object o) {
        if (o == null || o instanceof DERPrintableString) {
            return (DERPrintableString)o;
        }
        if (o instanceof byte[]) {
            try {
                return (DERPrintableString)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERPrintableString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERPrintableString) {
            return getInstance(object);
        }
        return new DERPrintableString(ASN1OctetString.getInstance(object).getOctets());
    }
    
    DERPrintableString(final byte[] string) {
        this.string = string;
    }
    
    public DERPrintableString(final String s) {
        this(s, false);
    }
    
    public DERPrintableString(final String s, final boolean b) {
        if (b && !isPrintableString(s)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
        this.string = Strings.toByteArray(s);
    }
    
    public String getString() {
        return Strings.fromByteArray(this.string);
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
        asn1OutputStream.writeEncoded(19, this.string);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof DERPrintableString && Arrays.areEqual(this.string, ((DERPrintableString)asn1Primitive).string);
    }
    
    @Override
    public String toString() {
        return this.getString();
    }
    
    public static boolean isPrintableString(final String s) {
        for (int i = s.length() - 1; i >= 0; --i) {
            final char char1 = s.charAt(i);
            if (char1 > '\u007f') {
                return false;
            }
            if ('a' > char1 || char1 > 'z') {
                if ('A' > char1 || char1 > 'Z') {
                    if ('0' > char1 || char1 > '9') {
                        switch (char1) {
                            case 32:
                            case 39:
                            case 40:
                            case 41:
                            case 43:
                            case 44:
                            case 45:
                            case 46:
                            case 47:
                            case 58:
                            case 61:
                            case 63: {
                                break;
                            }
                            default: {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
