package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Object;

public class ValidationParams extends ASN1Object
{
    private DERBitString seed;
    private ASN1Integer pgenCounter;
    
    public static ValidationParams getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static ValidationParams getInstance(final Object o) {
        if (o instanceof ValidationParams) {
            return (ValidationParams)o;
        }
        if (o != null) {
            return new ValidationParams(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ValidationParams(final byte[] array, final int n) {
        if (array == null) {
            throw new IllegalArgumentException("'seed' cannot be null");
        }
        this.seed = new DERBitString(array);
        this.pgenCounter = new ASN1Integer(n);
    }
    
    public ValidationParams(final DERBitString seed, final ASN1Integer pgenCounter) {
        if (seed == null) {
            throw new IllegalArgumentException("'seed' cannot be null");
        }
        if (pgenCounter == null) {
            throw new IllegalArgumentException("'pgenCounter' cannot be null");
        }
        this.seed = seed;
        this.pgenCounter = pgenCounter;
    }
    
    private ValidationParams(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.seed = DERBitString.getInstance(asn1Sequence.getObjectAt(0));
        this.pgenCounter = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public byte[] getSeed() {
        return this.seed.getBytes();
    }
    
    public BigInteger getPgenCounter() {
        return this.pgenCounter.getPositiveValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.seed);
        asn1EncodableVector.add(this.pgenCounter);
        return new DERSequence(asn1EncodableVector);
    }
}
