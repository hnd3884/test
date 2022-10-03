package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class PKIResponse extends ASN1Object
{
    private final ASN1Sequence controlSequence;
    private final ASN1Sequence cmsSequence;
    private final ASN1Sequence otherMsgSequence;
    
    private PKIResponse(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.controlSequence = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(0));
        this.cmsSequence = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
        this.otherMsgSequence = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(2));
    }
    
    public static PKIResponse getInstance(final Object o) {
        if (o instanceof PKIResponse) {
            return (PKIResponse)o;
        }
        if (o != null) {
            return new PKIResponse(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static PKIResponse getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.controlSequence);
        asn1EncodableVector.add(this.cmsSequence);
        asn1EncodableVector.add(this.otherMsgSequence);
        return new DERSequence(asn1EncodableVector);
    }
    
    public ASN1Sequence getControlSequence() {
        return this.controlSequence;
    }
    
    public ASN1Sequence getCmsSequence() {
        return this.cmsSequence;
    }
    
    public ASN1Sequence getOtherMsgSequence() {
        return this.otherMsgSequence;
    }
}
