package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class AlgorithmIdentifier extends ASN1Object
{
    private ASN1ObjectIdentifier algorithm;
    private ASN1Encodable parameters;
    
    public static AlgorithmIdentifier getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static AlgorithmIdentifier getInstance(final Object o) {
        if (o instanceof AlgorithmIdentifier) {
            return (AlgorithmIdentifier)o;
        }
        if (o != null) {
            return new AlgorithmIdentifier(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public AlgorithmIdentifier(final ASN1ObjectIdentifier algorithm) {
        this.algorithm = algorithm;
    }
    
    public AlgorithmIdentifier(final ASN1ObjectIdentifier algorithm, final ASN1Encodable parameters) {
        this.algorithm = algorithm;
        this.parameters = parameters;
    }
    
    private AlgorithmIdentifier(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1 || asn1Sequence.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.algorithm = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() == 2) {
            this.parameters = asn1Sequence.getObjectAt(1);
        }
        else {
            this.parameters = null;
        }
    }
    
    public ASN1ObjectIdentifier getAlgorithm() {
        return this.algorithm;
    }
    
    public ASN1Encodable getParameters() {
        return this.parameters;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.algorithm);
        if (this.parameters != null) {
            asn1EncodableVector.add(this.parameters);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
