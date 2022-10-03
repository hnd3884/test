package org.bouncycastle.asn1;

import java.io.EOFException;
import org.bouncycastle.util.io.Streams;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.Arrays;

public abstract class ASN1BitString extends ASN1Primitive implements ASN1String
{
    private static final char[] table;
    protected final byte[] data;
    protected final int padBits;
    
    protected static int getPadBits(final int n) {
        int n2 = 0;
        for (int i = 3; i >= 0; --i) {
            if (i != 0) {
                if (n >> i * 8 != 0) {
                    n2 = (n >> i * 8 & 0xFF);
                    break;
                }
            }
            else if (n != 0) {
                n2 = (n & 0xFF);
                break;
            }
        }
        if (n2 == 0) {
            return 0;
        }
        int n3 = 1;
        while (((n2 <<= 1) & 0xFF) != 0x0) {
            ++n3;
        }
        return 8 - n3;
    }
    
    protected static byte[] getBytes(final int n) {
        if (n == 0) {
            return new byte[0];
        }
        int n2 = 4;
        for (int n3 = 3; n3 >= 1 && (n & 255 << n3 * 8) == 0x0; --n3) {
            --n2;
        }
        final byte[] array = new byte[n2];
        for (int i = 0; i < n2; ++i) {
            array[i] = (byte)(n >> i * 8 & 0xFF);
        }
        return array;
    }
    
    public ASN1BitString(final byte[] array, final int padBits) {
        if (array == null) {
            throw new NullPointerException("data cannot be null");
        }
        if (array.length == 0 && padBits != 0) {
            throw new IllegalArgumentException("zero length data with non-zero pad bits");
        }
        if (padBits > 7 || padBits < 0) {
            throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
        }
        this.data = Arrays.clone(array);
        this.padBits = padBits;
    }
    
    public String getString() {
        final StringBuffer sb = new StringBuffer("#");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ASN1OutputStream asn1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
        try {
            asn1OutputStream.writeObject(this);
        }
        catch (final IOException ex) {
            throw new ASN1ParsingException("Internal error encoding BitString: " + ex.getMessage(), ex);
        }
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        for (int i = 0; i != byteArray.length; ++i) {
            sb.append(ASN1BitString.table[byteArray[i] >>> 4 & 0xF]);
            sb.append(ASN1BitString.table[byteArray[i] & 0xF]);
        }
        return sb.toString();
    }
    
    public int intValue() {
        int n = 0;
        byte[] array = this.data;
        if (this.padBits > 0 && this.data.length <= 4) {
            array = derForm(this.data, this.padBits);
        }
        for (int n2 = 0; n2 != array.length && n2 != 4; ++n2) {
            n |= (array[n2] & 0xFF) << 8 * n2;
        }
        return n;
    }
    
    public byte[] getOctets() {
        if (this.padBits != 0) {
            throw new IllegalStateException("attempt to get non-octet aligned data from BIT STRING");
        }
        return Arrays.clone(this.data);
    }
    
    public byte[] getBytes() {
        return derForm(this.data, this.padBits);
    }
    
    public int getPadBits() {
        return this.padBits;
    }
    
    @Override
    public String toString() {
        return this.getString();
    }
    
    @Override
    public int hashCode() {
        return this.padBits ^ Arrays.hashCode(this.getBytes());
    }
    
    protected boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        if (!(asn1Primitive instanceof ASN1BitString)) {
            return false;
        }
        final ASN1BitString asn1BitString = (ASN1BitString)asn1Primitive;
        return this.padBits == asn1BitString.padBits && Arrays.areEqual(this.getBytes(), asn1BitString.getBytes());
    }
    
    protected static byte[] derForm(final byte[] array, final int n) {
        final byte[] clone = Arrays.clone(array);
        if (n > 0) {
            final byte[] array2 = clone;
            final int n2 = array.length - 1;
            array2[n2] &= (byte)(255 << n);
        }
        return clone;
    }
    
    static ASN1BitString fromInputStream(final int n, final InputStream inputStream) throws IOException {
        if (n < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        final int read = inputStream.read();
        final byte[] array = new byte[n - 1];
        if (array.length != 0) {
            if (Streams.readFully(inputStream, array) != array.length) {
                throw new EOFException("EOF encountered in middle of BIT STRING");
            }
            if (read > 0 && read < 8 && array[array.length - 1] != (byte)(array[array.length - 1] & 255 << read)) {
                return new DLBitString(array, read);
            }
        }
        return new DERBitString(array, read);
    }
    
    public ASN1Primitive getLoadedObject() {
        return this.toASN1Primitive();
    }
    
    @Override
    ASN1Primitive toDERObject() {
        return new DERBitString(this.data, this.padBits);
    }
    
    @Override
    ASN1Primitive toDLObject() {
        return new DLBitString(this.data, this.padBits);
    }
    
    @Override
    abstract void encode(final ASN1OutputStream p0) throws IOException;
    
    static {
        table = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
