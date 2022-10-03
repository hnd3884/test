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

public class DomainParameters extends ASN1Object
{
    private final ASN1Integer p;
    private final ASN1Integer g;
    private final ASN1Integer q;
    private final ASN1Integer j;
    private final ValidationParams validationParams;
    
    public static DomainParameters getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static DomainParameters getInstance(final Object o) {
        if (o instanceof DomainParameters) {
            return (DomainParameters)o;
        }
        if (o != null) {
            return new DomainParameters(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public DomainParameters(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final ValidationParams validationParams) {
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
        if (bigInteger4 != null) {
            this.j = new ASN1Integer(bigInteger4);
        }
        else {
            this.j = null;
        }
        this.validationParams = validationParams;
    }
    
    private DomainParameters(final ASN1Sequence asn1Sequence) {
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
        else {
            this.j = null;
        }
        if (asn1Encodable != null) {
            this.validationParams = ValidationParams.getInstance(asn1Encodable.toASN1Primitive());
        }
        else {
            this.validationParams = null;
        }
    }
    
    private static ASN1Encodable getNext(final Enumeration enumeration) {
        return enumeration.hasMoreElements() ? enumeration.nextElement() : null;
    }
    
    public BigInteger getP() {
        return this.p.getPositiveValue();
    }
    
    public BigInteger getG() {
        return this.g.getPositiveValue();
    }
    
    public BigInteger getQ() {
        return this.q.getPositiveValue();
    }
    
    public BigInteger getJ() {
        if (this.j == null) {
            return null;
        }
        return this.j.getPositiveValue();
    }
    
    public ValidationParams getValidationParams() {
        return this.validationParams;
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
        if (this.validationParams != null) {
            asn1EncodableVector.add(this.validationParams);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
