package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class DHParameter extends ASN1Object
{
    ASN1Integer p;
    ASN1Integer g;
    ASN1Integer l;
    
    public DHParameter(final BigInteger bigInteger, final BigInteger bigInteger2, final int n) {
        this.p = new ASN1Integer(bigInteger);
        this.g = new ASN1Integer(bigInteger2);
        if (n != 0) {
            this.l = new ASN1Integer(n);
        }
        else {
            this.l = null;
        }
    }
    
    public static DHParameter getInstance(final Object o) {
        if (o instanceof DHParameter) {
            return (DHParameter)o;
        }
        if (o != null) {
            return new DHParameter(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private DHParameter(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.p = ASN1Integer.getInstance(objects.nextElement());
        this.g = ASN1Integer.getInstance(objects.nextElement());
        if (objects.hasMoreElements()) {
            this.l = (ASN1Integer)objects.nextElement();
        }
        else {
            this.l = null;
        }
    }
    
    public BigInteger getP() {
        return this.p.getPositiveValue();
    }
    
    public BigInteger getG() {
        return this.g.getPositiveValue();
    }
    
    public BigInteger getL() {
        if (this.l == null) {
            return null;
        }
        return this.l.getPositiveValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.p);
        asn1EncodableVector.add(this.g);
        if (this.getL() != null) {
            asn1EncodableVector.add(this.l);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
