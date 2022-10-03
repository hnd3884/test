package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.ASN1Object;

public class OcspIdentifier extends ASN1Object
{
    private ResponderID ocspResponderID;
    private ASN1GeneralizedTime producedAt;
    
    public static OcspIdentifier getInstance(final Object o) {
        if (o instanceof OcspIdentifier) {
            return (OcspIdentifier)o;
        }
        if (o != null) {
            return new OcspIdentifier(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private OcspIdentifier(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.ocspResponderID = ResponderID.getInstance(asn1Sequence.getObjectAt(0));
        this.producedAt = (ASN1GeneralizedTime)asn1Sequence.getObjectAt(1);
    }
    
    public OcspIdentifier(final ResponderID ocspResponderID, final ASN1GeneralizedTime producedAt) {
        this.ocspResponderID = ocspResponderID;
        this.producedAt = producedAt;
    }
    
    public ResponderID getOcspResponderID() {
        return this.ocspResponderID;
    }
    
    public ASN1GeneralizedTime getProducedAt() {
        return this.producedAt;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.ocspResponderID);
        asn1EncodableVector.add(this.producedAt);
        return new DERSequence(asn1EncodableVector);
    }
}
