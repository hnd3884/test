package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class SCVPReqRes extends ASN1Object
{
    private final ContentInfo request;
    private final ContentInfo response;
    
    public static SCVPReqRes getInstance(final Object o) {
        if (o instanceof SCVPReqRes) {
            return (SCVPReqRes)o;
        }
        if (o != null) {
            return new SCVPReqRes(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private SCVPReqRes(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.request = ContentInfo.getInstance(ASN1TaggedObject.getInstance(asn1Sequence.getObjectAt(0)), true);
            this.response = ContentInfo.getInstance(asn1Sequence.getObjectAt(1));
        }
        else {
            this.request = null;
            this.response = ContentInfo.getInstance(asn1Sequence.getObjectAt(0));
        }
    }
    
    public SCVPReqRes(final ContentInfo response) {
        this.request = null;
        this.response = response;
    }
    
    public SCVPReqRes(final ContentInfo request, final ContentInfo response) {
        this.request = request;
        this.response = response;
    }
    
    public ContentInfo getRequest() {
        return this.request;
    }
    
    public ContentInfo getResponse() {
        return this.response;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.request != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.request));
        }
        asn1EncodableVector.add(this.response);
        return new DERSequence(asn1EncodableVector);
    }
}
