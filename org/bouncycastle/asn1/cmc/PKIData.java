package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class PKIData extends ASN1Object
{
    private final TaggedAttribute[] controlSequence;
    private final TaggedRequest[] reqSequence;
    private final TaggedContentInfo[] cmsSequence;
    private final OtherMsg[] otherMsgSequence;
    
    public PKIData(final TaggedAttribute[] controlSequence, final TaggedRequest[] reqSequence, final TaggedContentInfo[] cmsSequence, final OtherMsg[] otherMsgSequence) {
        this.controlSequence = controlSequence;
        this.reqSequence = reqSequence;
        this.cmsSequence = cmsSequence;
        this.otherMsgSequence = otherMsgSequence;
    }
    
    private PKIData(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 4) {
            throw new IllegalArgumentException("Sequence not 4 elements.");
        }
        final ASN1Sequence asn1Sequence2 = (ASN1Sequence)asn1Sequence.getObjectAt(0);
        this.controlSequence = new TaggedAttribute[asn1Sequence2.size()];
        for (int i = 0; i < this.controlSequence.length; ++i) {
            this.controlSequence[i] = TaggedAttribute.getInstance(asn1Sequence2.getObjectAt(i));
        }
        final ASN1Sequence asn1Sequence3 = (ASN1Sequence)asn1Sequence.getObjectAt(1);
        this.reqSequence = new TaggedRequest[asn1Sequence3.size()];
        for (int j = 0; j < this.reqSequence.length; ++j) {
            this.reqSequence[j] = TaggedRequest.getInstance(asn1Sequence3.getObjectAt(j));
        }
        final ASN1Sequence asn1Sequence4 = (ASN1Sequence)asn1Sequence.getObjectAt(2);
        this.cmsSequence = new TaggedContentInfo[asn1Sequence4.size()];
        for (int k = 0; k < this.cmsSequence.length; ++k) {
            this.cmsSequence[k] = TaggedContentInfo.getInstance(asn1Sequence4.getObjectAt(k));
        }
        final ASN1Sequence asn1Sequence5 = (ASN1Sequence)asn1Sequence.getObjectAt(3);
        this.otherMsgSequence = new OtherMsg[asn1Sequence5.size()];
        for (int l = 0; l < this.otherMsgSequence.length; ++l) {
            this.otherMsgSequence[l] = OtherMsg.getInstance(asn1Sequence5.getObjectAt(l));
        }
    }
    
    public static PKIData getInstance(final Object o) {
        if (o instanceof PKIData) {
            return (PKIData)o;
        }
        if (o != null) {
            return new PKIData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[] { new DERSequence(this.controlSequence), new DERSequence(this.reqSequence), new DERSequence(this.cmsSequence), new DERSequence(this.otherMsgSequence) });
    }
    
    public TaggedAttribute[] getControlSequence() {
        return this.controlSequence;
    }
    
    public TaggedRequest[] getReqSequence() {
        return this.reqSequence;
    }
    
    public TaggedContentInfo[] getCmsSequence() {
        return this.cmsSequence;
    }
    
    public OtherMsg[] getOtherMsgSequence() {
        return this.otherMsgSequence;
    }
}
