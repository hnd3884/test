package org.bouncycastle.asn1.oiw;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class ElGamalParameter extends ASN1Object
{
    ASN1Integer p;
    ASN1Integer g;
    
    public ElGamalParameter(final BigInteger bigInteger, final BigInteger bigInteger2) {
        this.p = new ASN1Integer(bigInteger);
        this.g = new ASN1Integer(bigInteger2);
    }
    
    private ElGamalParameter(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.p = (ASN1Integer)objects.nextElement();
        this.g = (ASN1Integer)objects.nextElement();
    }
    
    public static ElGamalParameter getInstance(final Object o) {
        if (o instanceof ElGamalParameter) {
            return (ElGamalParameter)o;
        }
        if (o != null) {
            return new ElGamalParameter(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public BigInteger getP() {
        return this.p.getPositiveValue();
    }
    
    public BigInteger getG() {
        return this.g.getPositiveValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.p);
        asn1EncodableVector.add(this.g);
        return new DERSequence(asn1EncodableVector);
    }
}
