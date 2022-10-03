package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;

public class AttCertValidityPeriod extends ASN1Object
{
    ASN1GeneralizedTime notBeforeTime;
    ASN1GeneralizedTime notAfterTime;
    
    public static AttCertValidityPeriod getInstance(final Object o) {
        if (o instanceof AttCertValidityPeriod) {
            return (AttCertValidityPeriod)o;
        }
        if (o != null) {
            return new AttCertValidityPeriod(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private AttCertValidityPeriod(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.notBeforeTime = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(0));
        this.notAfterTime = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public AttCertValidityPeriod(final ASN1GeneralizedTime notBeforeTime, final ASN1GeneralizedTime notAfterTime) {
        this.notBeforeTime = notBeforeTime;
        this.notAfterTime = notAfterTime;
    }
    
    public ASN1GeneralizedTime getNotBeforeTime() {
        return this.notBeforeTime;
    }
    
    public ASN1GeneralizedTime getNotAfterTime() {
        return this.notAfterTime;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.notBeforeTime);
        asn1EncodableVector.add(this.notAfterTime);
        return new DERSequence(asn1EncodableVector);
    }
}
