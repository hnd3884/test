package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class OCSPRequest extends ASN1Object
{
    TBSRequest tbsRequest;
    Signature optionalSignature;
    
    public OCSPRequest(final TBSRequest tbsRequest, final Signature optionalSignature) {
        this.tbsRequest = tbsRequest;
        this.optionalSignature = optionalSignature;
    }
    
    private OCSPRequest(final ASN1Sequence asn1Sequence) {
        this.tbsRequest = TBSRequest.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() == 2) {
            this.optionalSignature = Signature.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(1), true);
        }
    }
    
    public static OCSPRequest getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static OCSPRequest getInstance(final Object o) {
        if (o instanceof OCSPRequest) {
            return (OCSPRequest)o;
        }
        if (o != null) {
            return new OCSPRequest(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public TBSRequest getTbsRequest() {
        return this.tbsRequest;
    }
    
    public Signature getOptionalSignature() {
        return this.optionalSignature;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.tbsRequest);
        if (this.optionalSignature != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.optionalSignature));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
