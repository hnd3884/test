package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class ProtectedPart extends ASN1Object
{
    private PKIHeader header;
    private PKIBody body;
    
    private ProtectedPart(final ASN1Sequence asn1Sequence) {
        this.header = PKIHeader.getInstance(asn1Sequence.getObjectAt(0));
        this.body = PKIBody.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static ProtectedPart getInstance(final Object o) {
        if (o instanceof ProtectedPart) {
            return (ProtectedPart)o;
        }
        if (o != null) {
            return new ProtectedPart(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ProtectedPart(final PKIHeader header, final PKIBody body) {
        this.header = header;
        this.body = body;
    }
    
    public PKIHeader getHeader() {
        return this.header;
    }
    
    public PKIBody getBody() {
        return this.body;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.header);
        asn1EncodableVector.add(this.body);
        return new DERSequence(asn1EncodableVector);
    }
}
