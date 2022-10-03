package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BERApplicationSpecific extends ASN1ApplicationSpecific
{
    BERApplicationSpecific(final boolean b, final int n, final byte[] array) {
        super(b, n, array);
    }
    
    public BERApplicationSpecific(final int n, final ASN1Encodable asn1Encodable) throws IOException {
        this(true, n, asn1Encodable);
    }
    
    public BERApplicationSpecific(final boolean b, final int n, final ASN1Encodable asn1Encodable) throws IOException {
        super(b || asn1Encodable.toASN1Primitive().isConstructed(), n, getEncoding(b, asn1Encodable));
    }
    
    private static byte[] getEncoding(final boolean b, final ASN1Encodable asn1Encodable) throws IOException {
        final byte[] encoded = asn1Encodable.toASN1Primitive().getEncoded("BER");
        if (b) {
            return encoded;
        }
        final int lengthOfHeader = ASN1ApplicationSpecific.getLengthOfHeader(encoded);
        final byte[] array = new byte[encoded.length - lengthOfHeader];
        System.arraycopy(encoded, lengthOfHeader, array, 0, array.length);
        return array;
    }
    
    public BERApplicationSpecific(final int n, final ASN1EncodableVector asn1EncodableVector) {
        super(true, n, getEncodedVector(asn1EncodableVector));
    }
    
    private static byte[] getEncodedVector(final ASN1EncodableVector asn1EncodableVector) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != asn1EncodableVector.size(); ++i) {
            try {
                byteArrayOutputStream.write(((ASN1Object)asn1EncodableVector.get(i)).getEncoded("BER"));
            }
            catch (final IOException ex) {
                throw new ASN1ParsingException("malformed object: " + ex, ex);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        int n = 64;
        if (this.isConstructed) {
            n |= 0x20;
        }
        asn1OutputStream.writeTag(n, this.tag);
        asn1OutputStream.write(128);
        asn1OutputStream.write(this.octets);
        asn1OutputStream.write(0);
        asn1OutputStream.write(0);
    }
}
