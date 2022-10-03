package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class SignaturePolicyId extends ASN1Object
{
    private ASN1ObjectIdentifier sigPolicyId;
    private OtherHashAlgAndValue sigPolicyHash;
    private SigPolicyQualifiers sigPolicyQualifiers;
    
    public static SignaturePolicyId getInstance(final Object o) {
        if (o instanceof SignaturePolicyId) {
            return (SignaturePolicyId)o;
        }
        if (o != null) {
            return new SignaturePolicyId(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private SignaturePolicyId(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2 && asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.sigPolicyId = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.sigPolicyHash = OtherHashAlgAndValue.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() == 3) {
            this.sigPolicyQualifiers = SigPolicyQualifiers.getInstance(asn1Sequence.getObjectAt(2));
        }
    }
    
    public SignaturePolicyId(final ASN1ObjectIdentifier asn1ObjectIdentifier, final OtherHashAlgAndValue otherHashAlgAndValue) {
        this(asn1ObjectIdentifier, otherHashAlgAndValue, null);
    }
    
    public SignaturePolicyId(final ASN1ObjectIdentifier sigPolicyId, final OtherHashAlgAndValue sigPolicyHash, final SigPolicyQualifiers sigPolicyQualifiers) {
        this.sigPolicyId = sigPolicyId;
        this.sigPolicyHash = sigPolicyHash;
        this.sigPolicyQualifiers = sigPolicyQualifiers;
    }
    
    public ASN1ObjectIdentifier getSigPolicyId() {
        return new ASN1ObjectIdentifier(this.sigPolicyId.getId());
    }
    
    public OtherHashAlgAndValue getSigPolicyHash() {
        return this.sigPolicyHash;
    }
    
    public SigPolicyQualifiers getSigPolicyQualifiers() {
        return this.sigPolicyQualifiers;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.sigPolicyId);
        asn1EncodableVector.add(this.sigPolicyHash);
        if (this.sigPolicyQualifiers != null) {
            asn1EncodableVector.add(this.sigPolicyQualifiers);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
