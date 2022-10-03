package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.ASN1Object;

public class MonetaryLimit extends ASN1Object
{
    DERPrintableString currency;
    ASN1Integer amount;
    ASN1Integer exponent;
    
    public static MonetaryLimit getInstance(final Object o) {
        if (o == null || o instanceof MonetaryLimit) {
            return (MonetaryLimit)o;
        }
        if (o instanceof ASN1Sequence) {
            return new MonetaryLimit(ASN1Sequence.getInstance(o));
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }
    
    private MonetaryLimit(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        this.currency = DERPrintableString.getInstance(objects.nextElement());
        this.amount = ASN1Integer.getInstance(objects.nextElement());
        this.exponent = ASN1Integer.getInstance(objects.nextElement());
    }
    
    public MonetaryLimit(final String s, final int n, final int n2) {
        this.currency = new DERPrintableString(s, true);
        this.amount = new ASN1Integer(n);
        this.exponent = new ASN1Integer(n2);
    }
    
    public String getCurrency() {
        return this.currency.getString();
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
