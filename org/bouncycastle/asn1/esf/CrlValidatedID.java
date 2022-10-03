package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CrlValidatedID extends ASN1Object
{
    private OtherHash crlHash;
    private CrlIdentifier crlIdentifier;
    
    public static CrlValidatedID getInstance(final Object o) {
        if (o instanceof CrlValidatedID) {
            return (CrlValidatedID)o;
        }
        if (o != null) {
            return new CrlValidatedID(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private CrlValidatedID(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1 || asn1Sequence.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.crlHash = OtherHash.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() > 1) {
            this.crlIdentifier = CrlIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        }
    }
    
    public CrlValidatedID(final OtherHash otherHash) {
        this(otherHash, null);
    }
    
    public CrlValidatedID(final OtherHash crlHash, final CrlIdentifier crlIdentifier) {
        this.crlHash = crlHash;
        this.crlIdentifier = crlIdentifier;
    }
    
    public OtherHash getCrlHash() {
        return this.crlHash;
    }
    
    public CrlIdentifier getCrlIdentifier() {
        return this.crlIdentifier;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.crlHash.toASN1Primitive());
        if (null != this.crlIdentifier) {
            asn1EncodableVector.add(this.crlIdentifier.toASN1Primitive());
        }
        return new DERSequence(asn1EncodableVector);
    }
}
