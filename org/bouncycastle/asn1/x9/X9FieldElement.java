package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.asn1.ASN1Object;

public class X9FieldElement extends ASN1Object
{
    protected ECFieldElement f;
    private static X9IntegerConverter converter;
    
    public X9FieldElement(final ECFieldElement f) {
        this.f = f;
    }
    
    public X9FieldElement(final BigInteger bigInteger, final ASN1OctetString asn1OctetString) {
        this(new ECFieldElement.Fp(bigInteger, new BigInteger(1, asn1OctetString.getOctets())));
    }
    
    public X9FieldElement(final int n, final int n2, final int n3, final int n4, final ASN1OctetString asn1OctetString) {
        this(new ECFieldElement.F2m(n, n2, n3, n4, new BigInteger(1, asn1OctetString.getOctets())));
    }
    
    public ECFieldElement getValue() {
        return this.f;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(X9FieldElement.converter.integerToBytes(this.f.toBigInteger(), X9FieldElement.converter.getByteLength(this.f)));
    }
    
    static {
        X9FieldElement.converter = new X9IntegerConverter();
    }
}
