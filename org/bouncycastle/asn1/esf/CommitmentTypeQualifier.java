package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class CommitmentTypeQualifier extends ASN1Object
{
    private ASN1ObjectIdentifier commitmentTypeIdentifier;
    private ASN1Encodable qualifier;
    
    public CommitmentTypeQualifier(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        this(asn1ObjectIdentifier, null);
    }
    
    public CommitmentTypeQualifier(final ASN1ObjectIdentifier commitmentTypeIdentifier, final ASN1Encodable qualifier) {
        this.commitmentTypeIdentifier = commitmentTypeIdentifier;
        this.qualifier = qualifier;
    }
    
    private CommitmentTypeQualifier(final ASN1Sequence asn1Sequence) {
        this.commitmentTypeIdentifier = (ASN1ObjectIdentifier)asn1Sequence.getObjectAt(0);
        if (asn1Sequence.size() > 1) {
            this.qualifier = asn1Sequence.getObjectAt(1);
        }
    }
    
    public static CommitmentTypeQualifier getInstance(final Object o) {
        if (o instanceof CommitmentTypeQualifier) {
            return (CommitmentTypeQualifier)o;
        }
        if (o != null) {
            return new CommitmentTypeQualifier(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1ObjectIdentifier getCommitmentTypeIdentifier() {
        return this.commitmentTypeIdentifier;
    }
    
    public ASN1Encodable getQualifier() {
        return this.qualifier;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.commitmentTypeIdentifier);
        if (this.qualifier != null) {
            asn1EncodableVector.add(this.qualifier);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
