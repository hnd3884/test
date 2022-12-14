package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class PolicyInformation extends ASN1Object
{
    private ASN1ObjectIdentifier policyIdentifier;
    private ASN1Sequence policyQualifiers;
    
    private PolicyInformation(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1 || asn1Sequence.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.policyIdentifier = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() > 1) {
            this.policyQualifiers = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
        }
    }
    
    public PolicyInformation(final ASN1ObjectIdentifier policyIdentifier) {
        this.policyIdentifier = policyIdentifier;
    }
    
    public PolicyInformation(final ASN1ObjectIdentifier policyIdentifier, final ASN1Sequence policyQualifiers) {
        this.policyIdentifier = policyIdentifier;
        this.policyQualifiers = policyQualifiers;
    }
    
    public static PolicyInformation getInstance(final Object o) {
        if (o == null || o instanceof PolicyInformation) {
            return (PolicyInformation)o;
        }
        return new PolicyInformation(ASN1Sequence.getInstance(o));
    }
    
    public ASN1ObjectIdentifier getPolicyIdentifier() {
        return this.policyIdentifier;
    }
    
    public ASN1Sequence getPolicyQualifiers() {
        return this.policyQualifiers;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.policyIdentifier);
        if (this.policyQualifiers != null) {
            asn1EncodableVector.add(this.policyQualifiers);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Policy information: ");
        sb.append(this.policyIdentifier);
        if (this.policyQualifiers != null) {
            final StringBuffer sb2 = new StringBuffer();
            for (int i = 0; i < this.policyQualifiers.size(); ++i) {
                if (sb2.length() != 0) {
                    sb2.append(", ");
                }
                sb2.append(PolicyQualifierInfo.getInstance(this.policyQualifiers.getObjectAt(i)));
            }
            sb.append("[");
            sb.append(sb2);
            sb.append("]");
        }
        return sb.toString();
    }
}
