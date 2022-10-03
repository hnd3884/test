package jcifs.spnego.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DERBitString extends DERObject
{
    protected byte[] data;
    protected int padBits;
    
    protected static int getPadBits(final int bitString) {
        if (bitString == 0) {
            return 7;
        }
        int val;
        if (bitString > 255) {
            val = (bitString >> 8 & 0xFF);
        }
        else {
            val = (bitString & 0xFF);
        }
        int bits = 1;
        while (((val <<= 1) & 0xFF) != 0x0) {
            ++bits;
        }
        return 8 - bits;
    }
    
    protected static byte[] getBytes(final int bitString) {
        if (bitString > 255) {
            final byte[] bytes = { (byte)(bitString & 0xFF), (byte)(bitString >> 8 & 0xFF) };
            return bytes;
        }
        final byte[] bytes = { (byte)(bitString & 0xFF) };
        return bytes;
    }
    
    public static DERBitString getInstance(final Object obj) {
        if (obj == null || obj instanceof DERBitString) {
            return (DERBitString)obj;
        }
        if (obj instanceof ASN1OctetString) {
            final byte[] bytes = ((ASN1OctetString)obj).getOctets();
            final int padBits = bytes[0];
            final byte[] data = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, data, 0, bytes.length - 1);
            return new DERBitString(data, padBits);
        }
        if (obj instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)obj).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERBitString getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    protected DERBitString(final byte data, final int padBits) {
        (this.data = new byte[1])[0] = data;
        this.padBits = padBits;
    }
    
    public DERBitString(final int data) {
        this(getBytes(data), getPadBits(data));
    }
    
    public DERBitString(final byte[] data, final int padBits) {
        this.data = data;
        this.padBits = padBits;
    }
    
    public DERBitString(final byte[] data) {
        this(data, 0);
    }
    
    public DERBitString(final DEREncodable obj) {
        try {
            final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            final DEROutputStream dOut = new DEROutputStream(bOut);
            dOut.writeObject(obj);
            dOut.close();
            this.data = bOut.toByteArray();
            this.padBits = 0;
        }
        catch (final IOException e) {
            throw new IllegalArgumentException("Error processing object : " + e.toString());
        }
    }
    
    public byte[] getBytes() {
        return this.data;
    }
    
    public int getPadBits() {
        return this.padBits;
    }
    
    void encode(final DEROutputStream out) throws IOException {
        final byte[] bytes = new byte[this.getBytes().length + 1];
        bytes[0] = (byte)this.getPadBits();
        System.arraycopy(this.getBytes(), 0, bytes, 1, bytes.length - 1);
        out.writeEncoded(3, bytes);
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof DERBitString)) {
            return false;
        }
        final DERBitString other = (DERBitString)o;
        if (this.data.length != other.data.length) {
            return false;
        }
        for (int i = 0; i != this.data.length; ++i) {
            if (this.data[i] != other.data[i]) {
                return false;
            }
        }
        return this.padBits == other.padBits;
    }
}
