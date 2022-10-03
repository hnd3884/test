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

public class ECGOST3410ParamSetParameters extends ASN1Object
{
    ASN1Integer p;
    ASN1Integer q;
    ASN1Integer a;
    ASN1Integer b;
    ASN1Integer x;
    ASN1Integer y;
    
    public static ECGOST3410ParamSetParameters getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static ECGOST3410ParamSetParameters getInstance(final Object o) {
        if (o == null || o instanceof ECGOST3410ParamSetParameters) {
            return (ECGOST3410ParamSetParameters)o;
        }
        if (o instanceof ASN1Sequence) {
            return new ECGOST3410ParamSetParameters((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("Invalid GOST3410Parameter: " + o.getClass().getName());
    }
    
    public ECGOST3410ParamSetParameters(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final int n, final BigInteger bigInteger5) {
        this.a = new ASN1Integer(bigInteger);
        this.b = new ASN1Integer(bigInteger2);
        this.p = new ASN1Integer(bigInteger3);
        this.q = new ASN1Integer(bigInteger4);
        this.x = new ASN1Integer(n);
        this.y = new ASN1Integer(bigInteger5);
    }
    
    public ECGOST3410ParamSetParameters(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.a = (ASN1Integer)objects.nextElement();
        this.b = (ASN1Integer)objects.nextElement();
        this.p = (ASN1Integer)objects.nextElement();
        this.q = (ASN1Integer)objects.nextElement();
        this.x = (ASN1Integer)objects.nextElement();
        this.y = (ASN1Integer)objects.nextElement();
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
        asn1EncodableVector.add(this.a);
        asn1EncodableVector.add(this.b);
        asn1EncodableVector.add(this.p);
        asn1EncodableVector.add(this.q);
        asn1EncodableVector.add(this.x);
        asn1EncodableVector.add(this.y);
        return new DERSequence(asn1EncodableVector);
    }
}
