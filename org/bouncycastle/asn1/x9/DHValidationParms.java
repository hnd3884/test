package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Object;

public class DHValidationParms extends ASN1Object
{
    private DERBitString seed;
    private ASN1Integer pgenCounter;
    
    public static DHValidationParms getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static DHValidationParms getInstance(final Object o) {
        if (o instanceof DHValidationParms) {
            return (DHValidationParms)o;
        }
        if (o != null) {
            return new DHValidationParms(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public DHValidationParms(final DERBitString seed, final ASN1Integer pgenCounter) {
        if (seed == null) {
            throw new IllegalArgumentException("'seed' cannot be null");
        }
        if (pgenCounter == null) {
            throw new IllegalArgumentException("'pgenCounter' cannot be null");
        }
        this.seed = seed;
        this.pgenCounter = pgenCounter;
    }
    
    private DHValidationParms(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.seed = DERBitString.getInstance(asn1Sequence.getObjectAt(0));
        this.pgenCounter = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public DERBitString getSeed() {
        return this.seed;
    }
    
    public ASN1Integer getPgenCounter() {
        return this.pgenCounter;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.seed);
        asn1EncodableVector.add(this.pgenCounter);
        return new DERSequence(asn1EncodableVector);
    }
}
