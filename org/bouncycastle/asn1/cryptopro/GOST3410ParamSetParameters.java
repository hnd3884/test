package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class GOST3410ParamSetParameters extends ASN1Object
{
    int keySize;
    ASN1Integer p;
    ASN1Integer q;
    ASN1Integer a;
    
    public static GOST3410ParamSetParameters getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static GOST3410ParamSetParameters getInstance(final Object o) {
        if (o == null || o instanceof GOST3410ParamSetParameters) {
            return (GOST3410ParamSetParameters)o;
        }
        if (o instanceof ASN1Sequence) {
            return new GOST3410ParamSetParameters((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("Invalid GOST3410Parameter: " + o.getClass().getName());
    }
    
    public GOST3410ParamSetParameters(final int keySize, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        this.keySize = keySize;
        this.p = new ASN1Integer(bigInteger);
        this.q = new ASN1Integer(bigInteger2);
        this.a = new ASN1Integer(bigInteger3);
    }
    
    public GOST3410ParamSetParameters(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.keySize = ((ASN1Integer)objects.nextElement()).getValue().intValue();
        this.p = (ASN1Integer)objects.nextElement();
        this.q = (ASN1Integer)objects.nextElement();
        this.a = (ASN1Integer)objects.nextElement();
    }
    
    @Deprecated
    public int getLKeySize() {
        return this.keySize;
    }
    
    public int getKeySize() {
        return this.keySize;
    }
    
    public BigInteger getP() {
        return this.p.getPositiveValue();
    }
    
    public BigInteger getQ() {
        return this.q.getPositiveValue();
    }
    
    public BigInteger getA() {
        return this.a.getPositiveValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(this.keySize));
        asn1EncodableVector.add(this.p);
        asn1EncodableVector.add(this.q);
        asn1EncodableVector.add(this.a);
        return new DERSequence(asn1EncodableVector);
    }
}
