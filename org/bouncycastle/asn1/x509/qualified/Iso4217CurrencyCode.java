package org.bouncycastle.asn1.x509.qualified;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class Iso4217CurrencyCode extends ASN1Object implements ASN1Choice
{
    final int ALPHABETIC_MAXSIZE = 3;
    final int NUMERIC_MINSIZE = 1;
    final int NUMERIC_MAXSIZE = 999;
    ASN1Encodable obj;
    int numeric;
    
    public static Iso4217CurrencyCode getInstance(final Object o) {
        if (o == null || o instanceof Iso4217CurrencyCode) {
            return (Iso4217CurrencyCode)o;
        }
        if (o instanceof ASN1Integer) {
            return new Iso4217CurrencyCode(ASN1Integer.getInstance(o).getValue().intValue());
        }
        if (o instanceof DERPrintableString) {
            return new Iso4217CurrencyCode(DERPrintableString.getInstance(o).getString());
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }
    
    public Iso4217CurrencyCode(final int n) {
        if (n > 999 || n < 1) {
            throw new IllegalArgumentException("wrong size in numeric code : not in (1..999)");
        }
        this.obj = new ASN1Integer(n);
    }
    
    public Iso4217CurrencyCode(final String s) {
        if (s.length() > 3) {
            throw new IllegalArgumentException("wrong size in alphabetic code : max size is 3");
        }
        this.obj = new DERPrintableString(s);
    }
    
    public boolean isAlphabetic() {
        return this.obj instanceof DERPrintableString;
    }
    
    public String getAlphabetic() {
        return ((DERPrintableString)this.obj).getString();
    }
    
    public int getNumeric() {
        return ((ASN1Integer)this.obj).getValue().intValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.obj.toASN1Primitive();
    }
}
