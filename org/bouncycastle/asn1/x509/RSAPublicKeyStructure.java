package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class RSAPublicKeyStructure extends ASN1Object
{
    private BigInteger modulus;
    private BigInteger publicExponent;
    
    public static RSAPublicKeyStructure getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static RSAPublicKeyStructure getInstance(final Object o) {
        if (o == null || o instanceof RSAPublicKeyStructure) {
            return (RSAPublicKeyStructure)o;
        }
        if (o instanceof ASN1Sequence) {
            return new RSAPublicKeyStructure((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("Invalid RSAPublicKeyStructure: " + o.getClass().getName());
    }
    
    public RSAPublicKeyStructure(final BigInteger modulus, final BigInteger publicExponent) {
        this.modulus = modulus;
        this.publicExponent = publicExponent;
    }
    
    public RSAPublicKeyStructure(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        this.modulus = ASN1Integer.getInstance(objects.nextElement()).getPositiveValue();
        this.publicExponent = ASN1Integer.getInstance(objects.nextElement()).getPositiveValue();
    }
    
    public BigInteger getModulus() {
        return this.modulus;
    }
    
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(this.getModulus()));
        asn1EncodableVector.add(new ASN1Integer(this.getPublicExponent()));
        return new DERSequence(asn1EncodableVector);
    }
}
