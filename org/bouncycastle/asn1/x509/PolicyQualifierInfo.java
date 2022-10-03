package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class PolicyQualifierInfo extends ASN1Object
{
    private ASN1ObjectIdentifier policyQualifierId;
    private ASN1Encodable qualifier;
    
    public PolicyQualifierInfo(final ASN1ObjectIdentifier policyQualifierId, final ASN1Encodable qualifier) {
        this.policyQualifierId = policyQualifierId;
        this.qualifier = qualifier;
    }
    
    public PolicyQualifierInfo(final String s) {
        this.policyQualifierId = PolicyQualifierId.id_qt_cps;
        this.qualifier = new DERIA5String(s);
    }
    
    @Deprecated
    public PolicyQualifierInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.policyQualifierId = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.qualifier = asn1Sequence.getObjectAt(1);
    }
    
    public static PolicyQualifierInfo getInstance(final Object o) {
        if (o instanceof PolicyQualifierInfo) {
            return (PolicyQualifierInfo)o;
        }
        if (o != null) {
            return new PolicyQualifierInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1ObjectIdentifier getPolicyQualifierId() {
        return this.policyQualifierId;
    }
    
    public ASN1Encodable getQualifier() {
        return this.qualifier;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.policyQualifierId);
        asn1EncodableVector.add(this.qualifier);
        return new DERSequence(asn1EncodableVector);
    }
}
