package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import java.util.Enumeration;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class DHDomainParameters extends ASN1Object
{
    private ASN1Integer p;
    private ASN1Integer g;
    private ASN1Integer q;
    private ASN1Integer j;
    private DHValidationParms validationParms;
    
    public static DHDomainParameters getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static DHDomainParameters getInstance(final Object o) {
        if (o == null || o instanceof DHDomainParameters) {
            return (DHDomainParameters)o;
        }
        if (o instanceof ASN1Sequence) {
            return new DHDomainParameters((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("Invalid DHDomainParameters: " + o.getClass().getName());
    }
    
    public DHDomainParameters(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final DHValidationParms validationParms) {
        if (bigInteger == null) {
            throw new IllegalArgumentException("'p' cannot be null");
        }
        if (bigInteger2 == null) {
            throw new IllegalArgumentException("'g' cannot be null");
        }
        if (bigInteger3 == null) {
            throw new IllegalArgumentException("'q' cannot be null");
        }
        this.p = new ASN1Integer(bigInteger);
        this.g = new ASN1Integer(bigInteger2);
        this.q = new ASN1Integer(bigInteger3);
        this.j = new ASN1Integer(bigInteger4);
        this.validationParms = validationParms;
    }
    
    public DHDomainParameters(final ASN1Integer p5, final ASN1Integer g, final ASN1Integer q, final ASN1Integer j, final DHValidationParms validationParms) {
        if (p5 == null) {
            throw new IllegalArgumentException("'p' cannot be null");
        }
        if (g == null) {
            throw new IllegalArgumentException("'g' cannot be null");
        }
        if (q == null) {
            throw new IllegalArgumentException("'q' cannot be null");
        }
        this.p = p5;
        this.g = g;
        this.q = q;
        this.j = j;
        this.validationParms = validationParms;
    }
    
    private DHDomainParameters(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 3 || asn1Sequence.size() > 5) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        this.p = ASN1Integer.getInstance(objects.nextElement());
        this.g = ASN1Integer.getInstance(objects.nextElement());
        this.q = ASN1Integer.getInstance(objects.nextElement());
        ASN1Encodable asn1Encodable = getNext(objects);
        if (asn1Encodable != null && asn1Encodable instanceof ASN1Integer) {
            this.j = ASN1Integer.getInstance(asn1Encodable);
            asn1Encodable = getNext(objects);
        }
        if (asn1Encodable != null) {
            this.validationParms = DHValidationParms.getInstance(asn1Encodable.toASN1Primitive());
        }
    }
    
    private static ASN1Encodable getNext(final Enumeration enumeration) {
        return enumeration.hasMoreElements() ? enumeration.nextElement() : null;
    }
    
    public ASN1Integer getP() {
        return this.p;
    }
    
    public ASN1Integer getG() {
        return this.g;
    }
    
    public ASN1Integer getQ() {
        return this.q;
    }
    
    public ASN1Integer getJ() {
        return this.j;
    }
    
    public DHValidationParms getValidationParms() {
        return this.validationParms;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.p);
        asn1EncodableVector.add(this.g);
        asn1EncodableVector.add(this.q);
        if (this.j != null) {
            asn1EncodableVector.add(this.j);
        }
        if (this.validationParms != null) {
            asn1EncodableVector.add(this.validationParms);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
