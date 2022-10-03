package org.bouncycastle.asn1.x509.qualified;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class MonetaryValue extends ASN1Object
{
    private Iso4217CurrencyCode currency;
    private ASN1Integer amount;
    private ASN1Integer exponent;
    
    public static MonetaryValue getInstance(final Object o) {
        if (o instanceof MonetaryValue) {
            return (MonetaryValue)o;
        }
        if (o != null) {
            return new MonetaryValue(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private MonetaryValue(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.currency = Iso4217CurrencyCode.getInstance(objects.nextElement());
        this.amount = ASN1Integer.getInstance(objects.nextElement());
        this.exponent = ASN1Integer.getInstance(objects.nextElement());
    }
    
    public MonetaryValue(final Iso4217CurrencyCode currency, final int n, final int n2) {
        this.currency = currency;
        this.amount = new ASN1Integer(n);
        this.exponent = new ASN1Integer(n2);
    }
    
    public Iso4217CurrencyCode getCurrency() {
        return this.currency;
    }
    
    public BigInteger getAmount() {
        return this.amount.getValue();
    }
    
    public BigInteger getExponent() {
        return this.exponent.getValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.currency);
        asn1EncodableVector.add(this.amount);
        asn1EncodableVector.add(this.exponent);
        return new DERSequence(asn1EncodableVector);
    }
}
