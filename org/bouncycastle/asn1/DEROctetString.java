package org.bouncycastle.asn1;

import java.io.IOException;

public class DEROctetString extends ASN1OctetString
{
    public DEROctetString(final byte[] array) {
        super(array);
    }
    
    public DEROctetString(final ASN1Encodable asn1Encodable) throws IOException {
        super(asn1Encodable.toASN1Primitive().getEncoded("DER"));
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
        asn1OutputStream.writeEncoded(4, this.string);
    }
    
    static void encode(final DEROutputStream derOutputStream, final byte[] array) throws IOException {
        derOutputStream.writeEncoded(4, array);
    }
}
