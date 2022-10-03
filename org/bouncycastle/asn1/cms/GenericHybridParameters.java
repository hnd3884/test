package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class GenericHybridParameters extends ASN1Object
{
    private final AlgorithmIdentifier kem;
    private final AlgorithmIdentifier dem;
    
    private GenericHybridParameters(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("ASN.1 SEQUENCE should be of length 2");
        }
        this.kem = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.dem = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static GenericHybridParameters getInstance(final Object o) {
        if (o instanceof GenericHybridParameters) {
            return (GenericHybridParameters)o;
        }
        if (o != null) {
            return new GenericHybridParameters(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public GenericHybridParameters(final AlgorithmIdentifier kem, final AlgorithmIdentifier dem) {
        this.kem = kem;
        this.dem = dem;
    }
    
    public AlgorithmIdentifier getDem() {
        return this.dem;
    }
    
    public AlgorithmIdentifier getKem() {
        return this.kem;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.kem);
        asn1EncodableVector.add(this.dem);
        return new DERSequence(asn1EncodableVector);
    }
}
