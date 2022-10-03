package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class DHPublicKey extends ASN1Object
{
    private ASN1Integer y;
    
    public static DHPublicKey getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Integer.getInstance(asn1TaggedObject, b));
    }
    
    public static DHPublicKey getInstance(final Object o) {
        if (o == null || o instanceof DHPublicKey) {
            return (DHPublicKey)o;
        }
        if (o instanceof ASN1Integer) {
            return new DHPublicKey((ASN1Integer)o);
        }
        throw new IllegalArgumentException("Invalid DHPublicKey: " + o.getClass().getName());
    }
    
    private DHPublicKey(final ASN1Integer y) {
        if (y == null) {
            throw new IllegalArgumentException("'y' cannot be null");
        }
        this.y = y;
    }
    
    public DHPublicKey(final BigInteger bigInteger) {
        if (bigInteger == null) {
            throw new IllegalArgumentException("'y' cannot be null");
        }
        this.y = new ASN1Integer(bigInteger);
    }
    
    public BigInteger getY() {
        return this.y.getPositiveValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.y;
    }
}
