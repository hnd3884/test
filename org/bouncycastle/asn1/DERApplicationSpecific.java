package org.bouncycastle.asn1;

import org.bouncycastle.util.encoders.Hex;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DERApplicationSpecific extends ASN1ApplicationSpecific
{
    DERApplicationSpecific(final boolean b, final int n, final byte[] array) {
        super(b, n, array);
    }
    
    public DERApplicationSpecific(final int n, final byte[] array) {
        this(false, n, array);
    }
    
    public DERApplicationSpecific(final int n, final ASN1Encodable asn1Encodable) throws IOException {
        this(true, n, asn1Encodable);
    }
    
    public DERApplicationSpecific(final boolean b, final int n, final ASN1Encodable asn1Encodable) throws IOException {
        super(b || asn1Encodable.toASN1Primitive().isConstructed(), n, getEncoding(b, asn1Encodable));
    }
    
    private static byte[] getEncoding(final boolean b, final ASN1Encodable asn1Encodable) throws IOException {
        final byte[] encoded = asn1Encodable.toASN1Primitive().getEncoded("DER");
        if (b) {
            return encoded;
        }
        final int lengthOfHeader = ASN1ApplicationSpecific.getLengthOfHeader(encoded);
        final byte[] array = new byte[encoded.length - lengthOfHeader];
        System.arraycopy(encoded, lengthOfHeader, array, 0, array.length);
        return array;
    }
    
    public DERApplicationSpecific(final int n, final ASN1EncodableVector asn1EncodableVector) {
        super(true, n, getEncodedVector(asn1EncodableVector));
    }
    
    private static byte[] getEncodedVector(final ASN1EncodableVector asn1EncodableVector) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != asn1EncodableVector.size(); ++i) {
            try {
                byteArrayOutputStream.write(((ASN1Object)asn1EncodableVector.get(i)).getEncoded("DER"));
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
        asn1OutputStream.writeEncoded(n, this.tag, this.octets);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("[");
        if (this.isConstructed()) {
            sb.append("CONSTRUCTED ");
        }
        sb.append("APPLICATION ");
        sb.append(Integer.toString(this.getApplicationTag()));
        sb.append("]");
        if (this.octets != null) {
            sb.append(" #");
            sb.append(Hex.toHexString(this.octets));
        }
        else {
            sb.append(" #null");
        }
        sb.append(" ");
        return sb.toString();
    }
}
