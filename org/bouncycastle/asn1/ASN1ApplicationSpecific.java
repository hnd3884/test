package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public abstract class ASN1ApplicationSpecific extends ASN1Primitive
{
    protected final boolean isConstructed;
    protected final int tag;
    protected final byte[] octets;
    
    ASN1ApplicationSpecific(final boolean isConstructed, final int tag, final byte[] array) {
        this.isConstructed = isConstructed;
        this.tag = tag;
        this.octets = Arrays.clone(array);
    }
    
    public static ASN1ApplicationSpecific getInstance(final Object o) {
        if (o == null || o instanceof ASN1ApplicationSpecific) {
            return (ASN1ApplicationSpecific)o;
        }
        if (o instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("Failed to construct object from byte[]: " + ex.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + o.getClass().getName());
    }
    
    protected static int getLengthOfHeader(final byte[] array) {
        final int n = array[1] & 0xFF;
        if (n == 128) {
            return 2;
        }
        if (n <= 127) {
            return 2;
        }
        final int n2 = n & 0x7F;
        if (n2 > 4) {
            throw new IllegalStateException("DER length more than 4 bytes: " + n2);
        }
        return n2 + 2;
    }
    
    public boolean isConstructed() {
        return this.isConstructed;
    }
    
    public byte[] getContents() {
        return Arrays.clone(this.octets);
    }
    
    public int getApplicationTag() {
        return this.tag;
    }
    
    public ASN1Primitive getObject() throws IOException {
        return ASN1Primitive.fromByteArray(this.getContents());
    }
    
    public ASN1Primitive getObject(final int n) throws IOException {
        if (n >= 31) {
            throw new IOException("unsupported tag number");
        }
        final byte[] encoded = this.getEncoded();
        final byte[] replaceTagNumber = this.replaceTagNumber(n, encoded);
        if ((encoded[0] & 0x20) != 0x0) {
            final byte[] array = replaceTagNumber;
            final int n2 = 0;
            array[n2] |= 0x20;
        }
        return ASN1Primitive.fromByteArray(replaceTagNumber);
    }
    
    @Override
    int encodedLength() throws IOException {
        return StreamUtil.calculateTagLength(this.tag) + StreamUtil.calculateBodyLength(this.octets.length) + this.octets.length;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        int n = 64;
        if (this.isConstructed) {
            n |= 0x20;
        }
        asn1OutputStream.writeEncoded(n, this.tag, this.octets);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        if (!(asn1Primitive instanceof ASN1ApplicationSpecific)) {
            return false;
        }
        final ASN1ApplicationSpecific asn1ApplicationSpecific = (ASN1ApplicationSpecific)asn1Primitive;
        return this.isConstructed == asn1ApplicationSpecific.isConstructed && this.tag == asn1ApplicationSpecific.tag && Arrays.areEqual(this.octets, asn1ApplicationSpecific.octets);
    }
    
    @Override
    public int hashCode() {
        return (this.isConstructed ? 1 : 0) ^ this.tag ^ Arrays.hashCode(this.octets);
    }
    
    private byte[] replaceTagNumber(final int n, final byte[] array) throws IOException {
        final int n2 = array[0] & 0x1F;
        int n3 = 1;
        if (n2 == 31) {
            int n4 = 0;
            int n5 = array[n3++] & 0xFF;
            if ((n5 & 0x7F) == 0x0) {
                throw new ASN1ParsingException("corrupted stream - invalid high tag number found");
            }
            while (n5 >= 0 && (n5 & 0x80) != 0x0) {
                n4 = (n4 | (n5 & 0x7F)) << 7;
                n5 = (array[n3++] & 0xFF);
            }
        }
        final byte[] array2 = new byte[array.length - n3 + 1];
        System.arraycopy(array, n3, array2, 1, array2.length - 1);
        array2[0] = (byte)n;
        return array2;
    }
}
