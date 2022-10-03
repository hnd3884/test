package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.DEREncodableVector;
import com.maverick.crypto.asn1.DERSequence;
import com.maverick.crypto.asn1.ASN1EncodableVector;
import com.maverick.crypto.asn1.DERObject;
import java.util.Enumeration;
import com.maverick.crypto.asn1.DERInteger;
import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.ASN1TaggedObject;
import java.math.BigInteger;
import com.maverick.crypto.asn1.DEREncodable;

public class RSAPublicKeyStructure implements DEREncodable
{
    private BigInteger b;
    private BigInteger c;
    
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
    
    public RSAPublicKeyStructure(final BigInteger b, final BigInteger c) {
        this.b = b;
        this.c = c;
    }
    
    public RSAPublicKeyStructure(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.b = ((DERInteger)objects.nextElement()).getPositiveValue();
        this.c = ((DERInteger)objects.nextElement()).getPositiveValue();
    }
    
    public BigInteger getModulus() {
        return this.b;
    }
    
    public BigInteger getPublicExponent() {
        return this.c;
    }
    
    public DERObject getDERObject() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new DERInteger(this.getModulus()));
        asn1EncodableVector.add(new DERInteger(this.getPublicExponent()));
        return new DERSequence(asn1EncodableVector);
    }
}
