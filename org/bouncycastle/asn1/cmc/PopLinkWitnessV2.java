package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class PopLinkWitnessV2 extends ASN1Object
{
    private final AlgorithmIdentifier keyGenAlgorithm;
    private final AlgorithmIdentifier macAlgorithm;
    private final byte[] witness;
    
    public PopLinkWitnessV2(final AlgorithmIdentifier keyGenAlgorithm, final AlgorithmIdentifier macAlgorithm, final byte[] array) {
        this.keyGenAlgorithm = keyGenAlgorithm;
        this.macAlgorithm = macAlgorithm;
        this.witness = Arrays.clone(array);
    }
    
    private PopLinkWitnessV2(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.keyGenAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.macAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.witness = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(2)).getOctets());
    }
    
    public static PopLinkWitnessV2 getInstance(final Object o) {
        if (o instanceof PopLinkWitnessV2) {
            return (PopLinkWitnessV2)o;
        }
        if (o != null) {
            return new PopLinkWitnessV2(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public AlgorithmIdentifier getKeyGenAlgorithm() {
        return this.keyGenAlgorithm;
    }
    
    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }
    
    public byte[] getWitness() {
        return Arrays.clone(this.witness);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.keyGenAlgorithm);
        asn1EncodableVector.add(this.macAlgorithm);
        asn1EncodableVector.add(new DEROctetString(this.getWitness()));
        return new DERSequence(asn1EncodableVector);
    }
}
