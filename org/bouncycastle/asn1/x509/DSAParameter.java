package org.bouncycastle.asn1.x509;

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

public class DSAParameter extends ASN1Object
{
    ASN1Integer p;
    ASN1Integer q;
    ASN1Integer g;
    
    public static DSAParameter getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static DSAParameter getInstance(final Object o) {
        if (o instanceof DSAParameter) {
            return (DSAParameter)o;
        }
        if (o != null) {
            return new DSAParameter(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public DSAParameter(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        this.p = new ASN1Integer(bigInteger);
        this.q = new ASN1Integer(bigInteger2);
        this.g = new ASN1Integer(bigInteger3);
    }
    
    private DSAParameter(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        this.p = ASN1Integer.getInstance(objects.nextElement());
        this.q = ASN1Integer.getInstance(objects.nextElement());
        this.g = ASN1Integer.getInstance(objects.nextElement());
    }
    
    public BigInteger getP() {
        return this.p.getPositiveValue();
    }
    
    public BigInteger getQ() {
        return this.q.getPositiveValue();
    }
    
    public BigInteger getG() {
        return this.g.getPositiveValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.p);
        asn1EncodableVector.add(this.q);
        asn1EncodableVector.add(this.g);
        return new DERSequence(asn1EncodableVector);
    }
}
