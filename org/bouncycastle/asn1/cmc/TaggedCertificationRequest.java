package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class TaggedCertificationRequest extends ASN1Object
{
    private final BodyPartID bodyPartID;
    private final CertificationRequest certificationRequest;
    
    public TaggedCertificationRequest(final BodyPartID bodyPartID, final CertificationRequest certificationRequest) {
        this.bodyPartID = bodyPartID;
        this.certificationRequest = certificationRequest;
    }
    
    private TaggedCertificationRequest(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(asn1Sequence.getObjectAt(0));
        this.certificationRequest = CertificationRequest.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static TaggedCertificationRequest getInstance(final Object o) {
        if (o instanceof TaggedCertificationRequest) {
            return (TaggedCertificationRequest)o;
        }
        if (o != null) {
            return new TaggedCertificationRequest(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static TaggedCertificationRequest getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.bodyPartID);
        asn1EncodableVector.add(this.certificationRequest);
        return new DERSequence(asn1EncodableVector);
    }
}
