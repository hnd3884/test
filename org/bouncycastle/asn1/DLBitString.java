package org.bouncycastle.asn1;

import java.io.IOException;

public class DLBitString extends ASN1BitString
{
    public static ASN1BitString getInstance(final Object o) {
        if (o == null || o instanceof DLBitString) {
            return (DLBitString)o;
        }
        if (o instanceof DERBitString) {
            return (DERBitString)o;
        }
        if (o instanceof byte[]) {
            try {
                return (ASN1BitString)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static ASN1BitString getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof DLBitString) {
            return getInstance(object);
        }
        return fromOctetString(((ASN1OctetString)object).getOctets());
    }
    
    protected DLBitString(final byte b, final int n) {
        this(toByteArray(b), n);
    }
    
    private static byte[] toByteArray(final byte b) {
        return new byte[] { b };
    }
    
    public DLBitString(final byte[] array, final int n) {
        super(array, n);
    }
    
    public DLBitString(final byte[] array) {
        this(array, 0);
    }
    
    public DLBitString(final int n) {
        super(ASN1BitString.getBytes(n), ASN1BitString.getPadBits(n));
    }
    
    public DLBitString(final ASN1Encodable asn1Encodable) throws IOException {
        super(asn1Encodable.toASN1Primitive().getEncoded("DER"), 0);
    }
    
    @Override
    boolean isConstructed() {
        return false;
    }
    
    @Override
    int encodedLength() {
        return 1 + StreamUtil.calculateBodyLength(this.data.length + 1) + this.data.length + 1;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        final byte[] data = this.data;
        final byte[] array = new byte[data.length + 1];
        array[0] = (byte)this.getPadBits();
        System.arraycopy(data, 0, array, 1, array.length - 1);
        asn1OutputStream.writeEncoded(3, array);
    }
    
    static DLBitString fromOctetString(final byte[] array) {
        if (array.length < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        final byte b = array[0];
        final byte[] array2 = new byte[array.length - 1];
        if (array2.length != 0) {
            System.arraycopy(array, 1, array2, 0, array.length - 1);
        }
        return new DLBitString(array2, b);
    }
}
