package org.bouncycastle.asn1;

import org.bouncycastle.util.Arrays;
import java.io.IOException;

public class ASN1Boolean extends ASN1Primitive
{
    private static final byte[] TRUE_VALUE;
    private static final byte[] FALSE_VALUE;
    private final byte[] value;
    public static final ASN1Boolean FALSE;
    public static final ASN1Boolean TRUE;
    
    public static ASN1Boolean getInstance(final Object o) {
        if (o == null || o instanceof ASN1Boolean) {
            return (ASN1Boolean)o;
        }
        if (o instanceof byte[]) {
            final byte[] array = (byte[])o;
            try {
                return (ASN1Boolean)ASN1Primitive.fromByteArray(array);
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("failed to construct boolean from byte[]: " + ex.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static ASN1Boolean getInstance(final boolean b) {
        return b ? ASN1Boolean.TRUE : ASN1Boolean.FALSE;
    }
    
    public static ASN1Boolean getInstance(final int n) {
        return (n != 0) ? ASN1Boolean.TRUE : ASN1Boolean.FALSE;
    }
    
    public static ASN1Boolean getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof ASN1Boolean) {
            return getInstance(object);
        }
        return fromOctetString(((ASN1OctetString)object).getOctets());
    }
    
    ASN1Boolean(final byte[] array) {
        if (array.length != 1) {
            throw new IllegalArgumentException("byte value should have 1 byte in it");
        }
        if (array[0] == 0) {
            this.value = ASN1Boolean.FALSE_VALUE;
        }
        else if ((array[0] & 0xFF) == 0xFF) {
            this.value = ASN1Boolean.TRUE_VALUE;
        }
        else {
            this.value = Arrays.clone(array);
        }
    }
    
    @Deprecated
    public ASN1Boolean(final boolean b) {
        this.value = (b ? ASN1Boolean.TRUE_VALUE : ASN1Boolean.FALSE_VALUE);
    }
    
    public boolean isTrue() {
        return this.value[0] != 0;
    }
    
    @Override
    boolean isConstructed() {
        return false;
    }
    
    @Override
    int encodedLength() {
        return 3;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.writeEncoded(1, this.value);
    }
    
    protected boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof ASN1Boolean && this.value[0] == ((ASN1Boolean)asn1Primitive).value[0];
    }
    
    @Override
    public int hashCode() {
        return this.value[0];
    }
    
    @Override
    public String toString() {
        return (this.value[0] != 0) ? "TRUE" : "FALSE";
    }
    
    static ASN1Boolean fromOctetString(final byte[] array) {
        if (array.length != 1) {
            throw new IllegalArgumentException("BOOLEAN value should have 1 byte in it");
        }
        if (array[0] == 0) {
            return ASN1Boolean.FALSE;
        }
        if ((array[0] & 0xFF) == 0xFF) {
            return ASN1Boolean.TRUE;
        }
        return new ASN1Boolean(array);
    }
    
    static {
        TRUE_VALUE = new byte[] { -1 };
        FALSE_VALUE = new byte[] { 0 };
        FALSE = new ASN1Boolean(false);
        TRUE = new ASN1Boolean(true);
    }
}
