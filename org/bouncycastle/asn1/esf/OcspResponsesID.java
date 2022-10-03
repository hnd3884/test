package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class OcspResponsesID extends ASN1Object
{
    private OcspIdentifier ocspIdentifier;
    private OtherHash ocspRepHash;
    
    public static OcspResponsesID getInstance(final Object o) {
        if (o instanceof OcspResponsesID) {
            return (OcspResponsesID)o;
        }
        if (o != null) {
            return new OcspResponsesID(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private OcspResponsesID(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1 || asn1Sequence.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.ocspIdentifier = OcspIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() > 1) {
            this.ocspRepHash = OtherHash.getInstance(asn1Sequence.getObjectAt(1));
        }
    }
    
    public OcspResponsesID(final OcspIdentifier ocspIdentifier) {
        this(ocspIdentifier, null);
    }
    
    public OcspResponsesID(final OcspIdentifier ocspIdentifier, final OtherHash ocspRepHash) {
        this.ocspIdentifier = ocspIdentifier;
        this.ocspRepHash = ocspRepHash;
    }
    
    public OcspIdentifier getOcspIdentifier() {
        return this.ocspIdentifier;
    }
    
    public OtherHash getOcspRepHash() {
        return this.ocspRepHash;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.ocspIdentifier);
        if (null != this.ocspRepHash) {
            asn1EncodableVector.add(this.ocspRepHash);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
