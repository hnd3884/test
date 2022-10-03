package org.bouncycastle.asn1;

import java.io.IOException;

public class DERTaggedObject extends ASN1TaggedObject
{
    private static final byte[] ZERO_BYTES;
    
    public DERTaggedObject(final boolean b, final int n, final ASN1Encodable asn1Encodable) {
        super(b, n, asn1Encodable);
    }
    
    public DERTaggedObject(final int n, final ASN1Encodable asn1Encodable) {
        super(true, n, asn1Encodable);
    }
    
    @Override
    boolean isConstructed() {
        return this.empty || this.explicit || this.obj.toASN1Primitive().toDERObject().isConstructed();
    }
    
    @Override
    int encodedLength() throws IOException {
        if (this.empty) {
            return StreamUtil.calculateTagLength(this.tagNo) + 1;
        }
        final int encodedLength = this.obj.toASN1Primitive().toDERObject().encodedLength();
        if (this.explicit) {
            return StreamUtil.calculateTagLength(this.tagNo) + StreamUtil.calculateBodyLength(encodedLength) + encodedLength;
        }
        return StreamUtil.calculateTagLength(this.tagNo) + (encodedLength - 1);
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        if (!this.empty) {
            final ASN1Primitive derObject = this.obj.toASN1Primitive().toDERObject();
            if (this.explicit) {
                asn1OutputStream.writeTag(160, this.tagNo);
                asn1OutputStream.writeLength(derObject.encodedLength());
                asn1OutputStream.writeObject(derObject);
            }
            else {
                int n;
                if (derObject.isConstructed()) {
                    n = 160;
                }
                else {
                    n = 128;
                }
                asn1OutputStream.writeTag(n, this.tagNo);
                asn1OutputStream.writeImplicitObject(derObject);
            }
        }
        else {
            asn1OutputStream.writeEncoded(160, this.tagNo, DERTaggedObject.ZERO_BYTES);
        }
    }
    
    static {
        ZERO_BYTES = new byte[0];
    }
}
