package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.Arrays;

public class DERUniversalString extends ASN1Primitive implements ASN1String
{
    private static final char[] table;
    private final byte[] string;
    
    public static DERUniversalString getInstance(final Object o) {
        if (o == null || o instanceof DERUniversalString) {
            return (DERUniversalString)o;
        }
        if (o instanceof byte[]) {
            try {
                return (DERUniversalString)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERUniversalString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DERUniversalString) {
            return getInstance(object);
        }
        return new DERUniversalString(((ASN1OctetString)object).getOctets());
    }
    
    public DERUniversalString(final byte[] array) {
        this.string = Arrays.clone(array);
    }
    
    public String getString() {
        final StringBuffer sb = new StringBuffer("#");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ASN1OutputStream asn1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
        try {
            asn1OutputStream.writeObject(this);
        }
        catch (final IOException ex) {
            throw new ASN1ParsingException("internal error encoding BitString");
        }
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        for (int i = 0; i != byteArray.length; ++i) {
            sb.append(DERUniversalString.table[byteArray[i] >>> 4 & 0xF]);
            sb.append(DERUniversalString.table[byteArray[i] & 0xF]);
        }
        return sb.toString();
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
        asn1OutputStream.writeEncoded(28, this.getOctets());
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof DERUniversalString && Arrays.areEqual(this.string, ((DERUniversalString)asn1Primitive).string);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
    
    static {
        table = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
