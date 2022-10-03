package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class OtherHashAlgAndValue extends ASN1Object
{
    private AlgorithmIdentifier hashAlgorithm;
    private ASN1OctetString hashValue;
    
    public static OtherHashAlgAndValue getInstance(final Object o) {
        if (o instanceof OtherHashAlgAndValue) {
            return (OtherHashAlgAndValue)o;
        }
        if (o != null) {
            return new OtherHashAlgAndValue(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private OtherHashAlgAndValue(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.hashAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.hashValue = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public OtherHashAlgAndValue(final AlgorithmIdentifier hashAlgorithm, final ASN1OctetString hashValue) {
        this.hashAlgorithm = hashAlgorithm;
        this.hashValue = hashValue;
    }
    
    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    public ASN1OctetString getHashValue() {
        return this.hashValue;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.hashAlgorithm);
        asn1EncodableVector.add(this.hashValue);
        return new DERSequence(asn1EncodableVector);
    }
}
