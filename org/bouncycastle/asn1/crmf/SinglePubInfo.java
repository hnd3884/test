package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class SinglePubInfo extends ASN1Object
{
    private ASN1Integer pubMethod;
    private GeneralName pubLocation;
    
    private SinglePubInfo(final ASN1Sequence asn1Sequence) {
        this.pubMethod = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() == 2) {
            this.pubLocation = GeneralName.getInstance(asn1Sequence.getObjectAt(1));
        }
    }
    
    public static SinglePubInfo getInstance(final Object o) {
        if (o instanceof SinglePubInfo) {
            return (SinglePubInfo)o;
        }
        if (o != null) {
            return new SinglePubInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public GeneralName getPubLocation() {
        return this.pubLocation;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.pubMethod);
        if (this.pubLocation != null) {
            asn1EncodableVector.add(this.pubLocation);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
