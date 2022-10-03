package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class PKIPublicationInfo extends ASN1Object
{
    private ASN1Integer action;
    private ASN1Sequence pubInfos;
    
    private PKIPublicationInfo(final ASN1Sequence asn1Sequence) {
        this.action = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
        this.pubInfos = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static PKIPublicationInfo getInstance(final Object o) {
        if (o instanceof PKIPublicationInfo) {
            return (PKIPublicationInfo)o;
        }
        if (o != null) {
            return new PKIPublicationInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Integer getAction() {
        return this.action;
    }
    
    public SinglePubInfo[] getPubInfos() {
        if (this.pubInfos == null) {
            return null;
        }
        final SinglePubInfo[] array = new SinglePubInfo[this.pubInfos.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = SinglePubInfo.getInstance(this.pubInfos.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.action);
        asn1EncodableVector.add(this.pubInfos);
        return new DERSequence(asn1EncodableVector);
    }
}
