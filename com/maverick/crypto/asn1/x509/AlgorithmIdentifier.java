package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.DEREncodableVector;
import com.maverick.crypto.asn1.DERSequence;
import com.maverick.crypto.asn1.ASN1EncodableVector;
import com.maverick.crypto.asn1.DERObject;
import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.ASN1TaggedObject;
import com.maverick.crypto.asn1.DEREncodable;
import com.maverick.crypto.asn1.DERObjectIdentifier;
import com.maverick.crypto.asn1.ASN1Encodable;

public class AlgorithmIdentifier extends ASN1Encodable
{
    private DERObjectIdentifier ic;
    private DEREncodable kc;
    private boolean jc;
    
    public static AlgorithmIdentifier getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static AlgorithmIdentifier getInstance(final Object o) {
        if (o instanceof AlgorithmIdentifier) {
            return (AlgorithmIdentifier)o;
        }
        if (o instanceof DERObjectIdentifier) {
            return new AlgorithmIdentifier((DERObjectIdentifier)o);
        }
        if (o instanceof String) {
            return new AlgorithmIdentifier((String)o);
        }
        if (o instanceof ASN1Sequence) {
            return new AlgorithmIdentifier((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("unknown object in factory");
    }
    
    public AlgorithmIdentifier(final DERObjectIdentifier ic) {
        this.jc = false;
        this.ic = ic;
    }
    
    public AlgorithmIdentifier(final String s) {
        this.jc = false;
        this.ic = new DERObjectIdentifier(s);
    }
    
    public AlgorithmIdentifier(final DERObjectIdentifier ic, final DEREncodable kc) {
        this.jc = false;
        this.jc = true;
        this.ic = ic;
        this.kc = kc;
    }
    
    public AlgorithmIdentifier(final ASN1Sequence asn1Sequence) {
        this.jc = false;
        this.ic = (DERObjectIdentifier)asn1Sequence.getObjectAt(0);
        if (asn1Sequence.size() == 2) {
            this.jc = true;
            this.kc = asn1Sequence.getObjectAt(1);
        }
        else {
            this.kc = null;
        }
    }
    
    public DERObjectIdentifier getObjectId() {
        return this.ic;
    }
    
    public DEREncodable getParameters() {
        return this.kc;
    }
    
    public DERObject toASN1Object() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.ic);
        if (this.jc) {
            asn1EncodableVector.add(this.kc);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
