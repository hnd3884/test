package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class OtherSigningCertificate extends ASN1Object
{
    ASN1Sequence certs;
    ASN1Sequence policies;
    
    public static OtherSigningCertificate getInstance(final Object o) {
        if (o instanceof OtherSigningCertificate) {
            return (OtherSigningCertificate)o;
        }
        if (o != null) {
            return new OtherSigningCertificate(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private OtherSigningCertificate(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1 || asn1Sequence.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.certs = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() > 1) {
            this.policies = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
        }
    }
    
    public OtherSigningCertificate(final OtherCertID otherCertID) {
        this.certs = new DERSequence(otherCertID);
    }
    
    public OtherCertID[] getCerts() {
        final OtherCertID[] array = new OtherCertID[this.certs.size()];
        for (int i = 0; i != this.certs.size(); ++i) {
            array[i] = OtherCertID.getInstance(this.certs.getObjectAt(i));
        }
        return array;
    }
    
    public PolicyInformation[] getPolicies() {
        if (this.policies == null) {
            return null;
        }
        final PolicyInformation[] array = new PolicyInformation[this.policies.size()];
        for (int i = 0; i != this.policies.size(); ++i) {
            array[i] = PolicyInformation.getInstance(this.policies.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certs);
        if (this.policies != null) {
            asn1EncodableVector.add(this.policies);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
