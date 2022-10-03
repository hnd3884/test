package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class SigPolicyQualifiers extends ASN1Object
{
    ASN1Sequence qualifiers;
    
    public static SigPolicyQualifiers getInstance(final Object o) {
        if (o instanceof SigPolicyQualifiers) {
            return (SigPolicyQualifiers)o;
        }
        if (o instanceof ASN1Sequence) {
            return new SigPolicyQualifiers(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private SigPolicyQualifiers(final ASN1Sequence qualifiers) {
        this.qualifiers = qualifiers;
    }
    
    public SigPolicyQualifiers(final SigPolicyQualifierInfo[] array) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < array.length; ++i) {
            asn1EncodableVector.add(array[i]);
        }
        this.qualifiers = new DERSequence(asn1EncodableVector);
    }
    
    public int size() {
        return this.qualifiers.size();
    }
    
    public SigPolicyQualifierInfo getInfoAt(final int n) {
        return SigPolicyQualifierInfo.getInstance(this.qualifiers.getObjectAt(n));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.qualifiers;
    }
}
